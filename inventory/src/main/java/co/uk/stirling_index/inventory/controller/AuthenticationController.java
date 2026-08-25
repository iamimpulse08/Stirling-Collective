package co.uk.stirling_index.inventory.controller;

import co.uk.stirling_index.inventory.model.security.dto.AuthResponse;
import co.uk.stirling_index.inventory.model.security.userdetails.AuthenticatedUser;
import co.uk.stirling_index.inventory.model.security.dto.LoginRequest;
import co.uk.stirling_index.inventory.model.security.dto.PromoteToBusinessAccountRequest;
import co.uk.stirling_index.inventory.model.security.userdetails.CustomUserPrinciple;
import co.uk.stirling_index.inventory.model.security.userdetails.User;
import co.uk.stirling_index.inventory.service.RefreshTokenService;
import co.uk.stirling_index.inventory.service.UserService;
import co.uk.stirling_index.inventory.service.security.JwtService;
import co.uk.stirling_index.inventory.service.repository.UserRepository;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(AuthenticationController.class);
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationController(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, UserService userService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PreAuthorize("hasRole('OPERATOR')")
    @PostMapping("/users/{userId}/escalate")
    public ResponseEntity<?> promoteToBusinessAccount
            (
                    @PathVariable UUID userId,
                    @RequestBody @Valid PromoteToBusinessAccountRequest request,
                    @AuthenticationPrincipal AuthenticatedUser user
            )
    {
        userService.promoteToBusinessAccount(userId, request.getBusinessId());

        return ResponseEntity.ok().build();
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User not found")
        );

        CustomUserPrinciple principle = new CustomUserPrinciple(user);
        String token = jwtService.generateAccessToken(principle);
        JwtService.RefreshTokenResult refreshToken = jwtService.generateRefreshToken(principle);

        userService.storeRefreshToken(user.getId(), refreshToken);
        JwtService.RefreshTokenResult refreshTokenResult = jwtService.generateRefreshToken(principle);

        refreshTokenService.store(user.getId(), refreshTokenResult.jti(), refreshTokenResult.expiration());


        ResponseCookie cookie =
                ResponseCookie.from("refresh_token", token)
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("None")
                        .path("/api/auth/refresh")
                        .maxAge(Duration.ofDays(30))
                        .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        logger.info("User logged in with ID: {} using email: {}, with role: {}",
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<JWTClaimsSet> claimsOptional = jwtService.parseAndValidate(refreshToken);
        if (claimsOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID jti = UUID.fromString(claimsOptional.get().getJWTID());
        Optional<User> userOptional = refreshTokenService.validateAndRotate(jti);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserPrinciple principle = new CustomUserPrinciple(userOptional.get());
        String newAccessToken = jwtService.generateAccessToken(principle);
        JwtService.RefreshTokenResult newRefreshToken = jwtService.generateRefreshToken(principle);

        refreshTokenService.store(userOptional.get().getId(), newRefreshToken.jti(), newRefreshToken.expiration());
        setRefreshCookie(response, newRefreshToken.accessToken(), newRefreshToken.expiration());

        return ResponseEntity.ok(new AuthResponse(newAccessToken));
    }

    @PostMapping("/logout")
    private ResponseEntity<?> logout(HttpServletResponse response,
                                          @CookieValue(value = "refresh_token", required = false) String refreshToken) {
        if (refreshToken != null) {
            jwtService.parseAndValidate(refreshToken).ifPresent(jwtClaimsSet -> {
                refreshTokenService.revoke(UUID.fromString(jwtClaimsSet.getJWTID()));
            });
        }
        clearRefreshCookie(response);
        return ResponseEntity.ok().build();
    }

    private void setRefreshCookie(HttpServletResponse response, String token, Instant expiration) {
        ResponseCookie cookie =
                ResponseCookie.from("refresh_token", token)
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("None")
                        .path("/api/auth/refresh")
                        .maxAge(Duration.between(Instant.now(), expiration))
                        .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/auth/refresh")
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
