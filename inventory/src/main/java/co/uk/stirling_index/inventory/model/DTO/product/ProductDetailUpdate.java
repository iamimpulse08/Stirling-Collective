package co.uk.stirling_index.inventory.model.DTO.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ProductDetailUpdate {

    @NotNull
    @PositiveOrZero
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @PositiveOrZero
    private Integer quantity;

    @PositiveOrZero
    private Long price;

    private String imageURI;
}
