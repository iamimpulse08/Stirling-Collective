package co.uk.stirling_index.inventory.model.security.userdetails;

import co.uk.stirling_index.inventory.model.business.Business;
import co.uk.stirling_index.inventory.model.security.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "business_id")
    private Business business;

    /*@Enumerated(EnumType.STRING)
    private Role role;*/
}


