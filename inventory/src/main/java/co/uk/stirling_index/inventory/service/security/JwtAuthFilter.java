package co.uk.stirling_index.inventory.service.security;

import co.uk.stirling_index.inventory.model.security.userdetails.AuthenticatedUser;
import co.uk.stirling_index.inventory.model.security.Role;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        Optional<JWTClaimsSet> claimsOptional = jwtService.parseAndValidate(token);

        // If the token is invalid or expired, return an error response with status code 401: Unauthorised.
        if (claimsOptional.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
            return;
        }

        JWTClaimsSet claims = claimsOptional.get();

        String email = claims.getSubject();
        Role role = Role.valueOf(claims.getClaim("role").toString());
        String businessIdClaim = claims.getStringClaim("businessId");
        UUID businessId = businessIdClaim != null ? UUID.fromString(businessIdClaim) : null;

        AuthenticatedUser principle = new AuthenticatedUser(email, role, businessId);

        var authToken = new UsernamePasswordAuthenticationToken(
                principle,
                null,
                List.of(
                        new SimpleGrantedAuthority(role.name())
                )
        );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
