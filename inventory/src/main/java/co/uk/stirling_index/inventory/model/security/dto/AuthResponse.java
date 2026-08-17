package co.uk.stirling_index.inventory.model.security.dto;

public record AuthResponse(String token) {

    public AuthResponse(String token) {
        this.token = token;
    }
}
