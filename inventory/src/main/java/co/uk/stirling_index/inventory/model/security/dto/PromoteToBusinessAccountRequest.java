package co.uk.stirling_index.inventory.model.security.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PromoteToBusinessAccountRequest {

    @NotNull(message = "Business ID is required")
    private UUID businessId;
}
