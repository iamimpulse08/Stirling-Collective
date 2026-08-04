package co.uk.stirling_index.inventory.service.security;

import co.uk.stirling_index.inventory.model.User;
import co.uk.stirling_index.inventory.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("businessSecurity")
@RequiredArgsConstructor
public class BusinessSecurity {

    private final UserRepository userRepository;

    public boolean isOwner(UUID businessId, Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow();

        if (user.getBusiness() == null || user.getBusiness().getId() == null) {
            return false;
        }

        return user.getBusiness().getId().equals(businessId);
    }
}
