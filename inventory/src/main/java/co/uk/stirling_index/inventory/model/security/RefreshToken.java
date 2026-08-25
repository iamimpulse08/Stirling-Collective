package co.uk.stirling_index.inventory.model.security;


import co.uk.stirling_index.inventory.model.security.userdetails.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshToken {

    /**
     * The JTI embedded within the JWT.
     */
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiration;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(nullable = false)
    private Instant created = Instant.now();
}
