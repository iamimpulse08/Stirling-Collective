package co.uk.stirling_index.inventory;

import co.uk.stirling_index.inventory.model.security.Role;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class JwtTestUtilities {

    private static final KeyPair keypair = generateKeyPair();

    private static KeyPair generateKeyPair() {

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to generate key pair", e);

        }
    }

    private static RSAPrivateKey getPrivateKey() {
        return (RSAPrivateKey) keypair.getPrivate();
    }

    private static RSAPublicKey getPublicKey() {
        return (RSAPublicKey) keypair.getPublic();
    }

    public static String getPrivateKeyAsBase64() {
        return Base64.getEncoder().encodeToString(getPrivateKey().getEncoded());
    }

    public static String getPublicKeyAsBase64() {
        return Base64.getEncoder().encodeToString(getPublicKey().getEncoded());
    }

    public static String getPublicKeyPEM() {
        return "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getEncoder().encodeToString(getPublicKey().getEncoded()) +
                "\n-----END PUBLIC KEY-----";
    }

    public static String generateToken(String email, Role role, UUID businessId) {
        return generateToken(email, role, businessId, Instant.now().plusSeconds(3600));
    }

    public static String generateExpiredToken(String email, Role role, UUID businessId) {
        return generateToken(email, role, businessId, Instant.now().minusSeconds(60));
    }

    private static String generateToken(String email, Role role, UUID businessId, Instant expiration) {
        try {
            JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                    .subject(email)
                    .claim("role", role)
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(Date.from(expiration));

            if (businessId != null) {
                claims.claim("businessId", businessId.toString());
            }

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).build(), claims.build()
            );

            jwt.sign(new RSASSASigner((RSAPrivateKey) keypair.getPrivate()));
            return jwt.serialize();
        }
        catch (JOSEException e) {
            throw new IllegalStateException("Failed to generate JWT: \n", e);
        }
    }


}
