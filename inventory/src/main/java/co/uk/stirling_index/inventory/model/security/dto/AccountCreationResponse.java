package co.uk.stirling_index.inventory.model.security.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AccountCreationResponse extends AbstractUserDetailDTO {
    private UUID id;
}
