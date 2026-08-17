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
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final RsaKeyProvider keyProvider;
    private static final long EXPIRATION_MS = 1000 * 60 * 30;

    public String generateToken(CustomUserPrinciple principle) {
        try {
            JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                    .subject(principle.getUsername())
                    .claim("role", principle.getRole().name())
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(Date.from(Instant.now().plusMillis(EXPIRATION_MS)));

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
