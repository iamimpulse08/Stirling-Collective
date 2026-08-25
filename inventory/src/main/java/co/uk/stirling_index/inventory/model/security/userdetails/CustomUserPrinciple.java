package co.uk.stirling_index.inventory.model.security.userdetails;

import co.uk.stirling_index.inventory.model.business.Business;
import co.uk.stirling_index.inventory.model.security.Role;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class CustomUserPrinciple implements org.springframework.security.core.userdetails.UserDetails {

    private final User user;

    public CustomUserPrinciple(User user) {
        this.user = user;
    }

    public Role getRole() {
        return user.getRole();
    }

    public UUID getBusinessId() {
        Business business = user.getBusiness();
        return business != null ? business.getId() : null;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public String getEmail() {
        return user.getEmail();
    }
}
