package co.uk.stirling_index.inventory.service.security;

import co.uk.stirling_index.inventory.model.security.userdetails.CustomUserPrinciple;
import co.uk.stirling_index.inventory.model.security.RsaKeyProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final RsaKeyProvider keyProvider;
    // 1 SECOND * 60 SECONDS * 15 MINUTES
    private static final long ACCESS_EXPIRATION_MS = 1000 * 60 * 15;
    // 1 SECOND * 60 SECONDS * 1 DAY * 30 DAYS
    private static final long REFRESH_EXPIRATION_MS = 1000 * 60 * 60 * 24 * 30;

    public String generateAccessToken(CustomUserPrinciple principle) {
        try {
            JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                    .subject(principle.getUsername())
                    .claim("role", principle.getRole().name())
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(Date.from(Instant.now().plusMillis(ACCESS_EXPIRATION_MS)));

            if (principle.getBusinessId() != null) {
                claims.claim("businessId", principle.getBusinessId());
            }

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build(), claims.build()
            );

            jwt.sign(new RSASSASigner(keyProvider.getPrivateKey()));
            return jwt.serialize();
        }
        catch (JOSEException e) {
            throw new IllegalStateException("Failed to sign JWT: ", e);
        }
    }

    /**
     * Returns the serialised JWT and the jti (JWT ID) of the JWT.
     * @param accessToken
     * @param jti
     * @param expiration
     */
    public record RefreshTokenResult(String accessToken, UUID jti, Instant expiration) {}

    public RefreshTokenResult generateRefreshToken(CustomUserPrinciple principle) {
        try {
            UUID jti = UUID.randomUUID();
            Instant expiration = Instant.now().plusMillis(REFRESH_EXPIRATION_MS);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(principle.getUsername())
                    .jwtID(jti.toString())
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(Date.from(expiration))
                    .build();

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build(), claims
            );
            jwt.sign(new RSASSASigner(keyProvider.getPrivateKey()));
            return new RefreshTokenResult(jwt.serialize(), jti, expiration);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate refresh token", e);
        }
    }

    /**
     * Parses a JWT and validates the signature and expiration time.
     * @param token - the JWT to parse and validate.
     * @return the JWTClaimsSet if the JWT is valid, otherwise an empty Optional.
     */
    public Optional<JWTClaimsSet> parseAndValidate(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            boolean validSignature = jwt.verify(new RSASSAVerifier(keyProvider.getPublicKey()));
            boolean notExpired = jwt.getJWTClaimsSet().getExpirationTime().after(new Date());

            return (validSignature && notExpired) ? Optional.of(jwt.getJWTClaimsSet()) : Optional.empty();
        }
        catch (ParseException | JOSEException e) {
            return Optional.empty();
        }
    }
}
