package co.uk.stirling_index.inventory.config;

import co.uk.stirling_index.inventory.service.security.ExtendedUserDetailsService;
import co.uk.stirling_index.inventory.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(ExtendedUserDetailsService extendedUserDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(extendedUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration AuthorisedConfig = new CorsConfiguration();
        AuthorisedConfig.setAllowedOrigins(List.of("https://admin.crowcuriosities.co.uk"));
        AuthorisedConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        AuthorisedConfig.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        AuthorisedConfig.setAllowCredentials(true);
        source.registerCorsConfiguration("/api/auth/**", AuthorisedConfig);

        CorsConfiguration publicConfig = new CorsConfiguration();
        publicConfig.setAllowedOrigins(List.of("*"));
        publicConfig.setAllowedMethods(List.of("GET"));
        publicConfig.setAllowedHeaders(List.of("Content-Type"));
        publicConfig.setAllowCredentials(false);
        source.registerCorsConfiguration("/api/products/**", publicConfig);

        return source;
    }
}
