package co.uk.stirling_index.inventory.model.security.dto;

import co.uk.stirling_index.inventory.model.security.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractUserDetailDTO {

    private String username;
    private String email;
    private String password;
}
