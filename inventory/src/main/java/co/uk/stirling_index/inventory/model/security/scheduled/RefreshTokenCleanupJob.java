package co.uk.stirling_index.inventory.model.security.scheduled;

import co.uk.stirling_index.inventory.service.RefreshTokenService;
import co.uk.stirling_index.inventory.service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenCleanupJob.class);
    private final RefreshTokenRepository refreshTokenRepository;

    // runs every day at 3am server-time.
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeExpiredAndRevokedTokens() {
        int deleted = refreshTokenRepository.deleteExpiredOrRevoked(Instant.now());
        if (deleted > 0) {
            logger.info("Deleted {} expired or revoked refresh tokens", deleted);
        }
    }
}
