package co.uk.stirling_index.inventory.controller;

import co.uk.stirling_index.inventory.model.DTO.security.AuthResponse;
import co.uk.stirling_index.inventory.model.DTO.security.LoginRequest;
import co.uk.stirling_index.inventory.model.DTO.security.RegisterRequest;
import co.uk.stirling_index.inventory.model.Role;
import co.uk.stirling_index.inventory.model.User;
import co.uk.stirling_index.inventory.service.security.JwtService;
import co.uk.stirling_index.inventory.service.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(AuthenticationController.class);
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
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
                user.getUuid(), user.getRole().name(), user.getBusiness().getName(), user.getBusiness().getId()
                );
        return ResponseEntity.ok(new AuthResponse(jwtService.generateToken(request.getEmail())));
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

        String token = jwtService.generateToken(email);

        logger.info("User logged in with ID: {} using email: {}, with role: {}",
                user.getUuid(), user.getEmail(), user.getRole().name()
        );

        return ResponseEntity.ok(new AuthResponse(token));
    }
}
