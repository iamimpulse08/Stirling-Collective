package co.uk.stirling_index.inventory.service.repository;

import co.uk.stirling_index.inventory.model.security.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    @Modifying
    @Query("UPDATE RefreshToken rtoken SET rtoken.revoked = true WHERE rtoken.user.id = :userId AND rtoken.revoked = false")
    void revokeAllForUser(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM RefreshToken rtoken WHERE rtoken.expiration < :expiration OR rtoken.revoked = true")
    int deleteExpiredOrRevoked(@Param("expiration") Instant expiration);
}
