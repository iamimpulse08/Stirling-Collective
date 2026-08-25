package co.uk.stirling_index.inventory.service;

import co.uk.stirling_index.inventory.model.security.RefreshToken;
import co.uk.stirling_index.inventory.model.security.userdetails.User;
import co.uk.stirling_index.inventory.service.repository.RefreshTokenRepository;
import co.uk.stirling_index.inventory.service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public void store(UUID userId, UUID jti, Instant expiration) {
        RefreshToken entity = new RefreshToken();
        entity.setId(jti);
        entity.setUser(userRepository.getReferenceById(userId));
        entity.setExpiration(expiration);
        refreshTokenRepository.save(entity);
    }

    @Transactional
    public Optional<User> validateAndRotate(UUID jti) {

        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findById(jti);
        if (refreshTokenOptional.isEmpty()) {
            return Optional.empty();
        }

        // if token is reused after being refreshed, revoke all tokens for the user - this is to prevent token fixation attacks.
        RefreshToken refreshToken = refreshTokenOptional.get();
        if (refreshToken.isRevoked()) {
            revokeAllForUser(refreshToken.getUser().getId());
            return Optional.empty();
        }

        if (refreshToken.getExpiration().isBefore(Instant.now())) {
            return Optional.empty();
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        return Optional.of(refreshToken.getUser());
    }

    /**
     * Revokes a given refresh token.
     * @param jti - the jti of the refresh token to revoke.
     */
    public void revoke(UUID jti) {
        refreshTokenRepository.findById(jti)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshTokenRepository.save(refreshToken);
                });
    }

    public void revokeAllForUser(UUID userId) {
        refreshTokenRepository.revokeAllForUser(userId);
    }


}
