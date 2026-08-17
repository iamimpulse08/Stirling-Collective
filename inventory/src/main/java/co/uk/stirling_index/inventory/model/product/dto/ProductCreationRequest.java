package co.uk.stirling_index.inventory.model.product.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductCreationRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @PositiveOrZero
    private Integer quantity;

    @PositiveOrZero
    private Long price;

    private String imageURI;

    /**
     * Checks whether the DTO has a name, category, quantity, and price.
     * @return true if all required fields are present, false otherwise.
     */
    public boolean hasAllRequiredFields() {
        return name != null && category != null && quantity != null && price != null;
    }
}
