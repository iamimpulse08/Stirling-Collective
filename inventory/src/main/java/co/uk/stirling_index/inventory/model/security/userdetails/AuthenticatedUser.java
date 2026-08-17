package co.uk.stirling_index.inventory.model.security.userdetails;

import co.uk.stirling_index.inventory.model.security.Role;

import java.util.UUID;

public record AuthenticatedUser(String email, Role role, UUID businessID) {
}
