package co.uk.stirling_index.inventory.service;

import co.uk.stirling_index.inventory.model.business.Business;
import co.uk.stirling_index.inventory.model.security.Role;
import co.uk.stirling_index.inventory.model.security.userdetails.User;
import co.uk.stirling_index.inventory.service.repository.BusinessRepository;
import co.uk.stirling_index.inventory.service.repository.UserRepository;
import co.uk.stirling_index.inventory.service.security.JwtService;
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

    public String createAccount(String email) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Account already exists for email: " + email);
        }

        String tempPassword = UUID.randomUUID().toString();

        User user = new User();
        user.setEmail(email);
        user.setPassword(tempPassword);
        user.setRole(Role.VIEWER);
        userRepository.save(user);

        return tempPassword;
    }
}
