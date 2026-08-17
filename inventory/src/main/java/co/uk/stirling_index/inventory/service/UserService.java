package co.uk.stirling_index.inventory.service;

import co.uk.stirling_index.inventory.model.business.Business;
import co.uk.stirling_index.inventory.model.security.Role;
import co.uk.stirling_index.inventory.model.security.userdetails.User;
import co.uk.stirling_index.inventory.service.repository.BusinessRepository;
import co.uk.stirling_index.inventory.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public void promoteToBusinessAccount(UUID userId, UUID businessId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId.toString()));

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new UsernameNotFoundException(businessId.toString()));

        user.setBusiness(business);
        user.setRole(Role.BUSINESS);
        userRepository.save(user);
    }
}
