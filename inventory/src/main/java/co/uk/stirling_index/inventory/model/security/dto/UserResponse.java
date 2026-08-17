package co.uk.stirling_index.inventory.model.security.dto;

import co.uk.stirling_index.inventory.model.security.userdetails.User;

import java.util.UUID;

public record UserResponse(UUID id, String username, String roleDisplayName) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole().getDisplayName());
    }
}
