package co.uk.stirling_index.inventory.controller;

import co.uk.stirling_index.inventory.model.security.userdetails.AuthenticatedUser;
import co.uk.stirling_index.inventory.model.security.dto.AuthResponse;
import co.uk.stirling_index.inventory.model.security.dto.LoginRequest;
import co.uk.stirling_index.inventory.model.security.dto.PromoteToBusinessAccountRequest;
import co.uk.stirling_index.inventory.model.security.dto.RegisterRequest;
import co.uk.stirling_index.inventory.model.security.Role;
import co.uk.stirling_index.inventory.model.security.userdetails.CustomUserPrinciple;
import co.uk.stirling_index.inventory.model.security.userdetails.User;
import co.uk.stirling_index.inventory.service.UserService;
import co.uk.stirling_index.inventory.service.security.JwtService;
import co.uk.stirling_index.inventory.service.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    public AuthenticationController(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    private User createNewUser(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.VIEWER);
        return user;
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        User user = createNewUser(request);

        logger.info("Created new user with ID: {} with role: {} to the database for business: {} with business ID: {}",
                user.getId(),
                user.getRole().name(),
                user.getBusiness().getName(),
                user.getBusiness().getId()
                );

        CustomUserPrinciple principle = new CustomUserPrinciple(user);
        String token = jwtService.generateToken(principle);

        return ResponseEntity.ok(new AuthResponse(token));
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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new UsernameNotFoundException("User not found with email:" + email)
        );

        CustomUserPrinciple principle = new CustomUserPrinciple(user);
        String token = jwtService.generateToken(principle);

        logger.info("User logged in with ID: {} using email: {}, with role: {}",
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(new AuthResponse(token));
    }
}
