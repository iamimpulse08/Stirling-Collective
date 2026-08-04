package co.uk.stirling_index.inventory.model.DTO.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractUserDetailDTO {

    private String username;
    private String email;
    private String role;
    private String password;
}
