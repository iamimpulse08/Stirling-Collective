package co.uk.stirling_index.inventory.model.security.dto;

import co.uk.stirling_index.inventory.model.security.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractUserDetailDTO {

    @NotBlank
    private String email;
    private String password;
}
