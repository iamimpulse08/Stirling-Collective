package co.uk.stirling_index.inventory.model.DTO.security;

public record AuthResponse(String token) {

    public AuthResponse(String token) {
        this.token = token;
    }
}
