package co.uk.stirling_index.inventory.model.security;

import org.springframework.http.HttpStatus;

public record AccessCase(Role role, HttpStatus status) {
}
