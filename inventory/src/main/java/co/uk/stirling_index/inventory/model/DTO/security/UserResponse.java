package co.uk.stirling_index.inventory.model.DTO.security;

import java.util.UUID;

public record UserResponse(UUID id, String username, String roleDisplayName) {
    public static UserResponse from(co.uk.stirling_index.inventory.model.User user) {
        return new UserResponse(user.getUuid(), user.getEmail(), user.getRole().getDisplayName());
    }
}
