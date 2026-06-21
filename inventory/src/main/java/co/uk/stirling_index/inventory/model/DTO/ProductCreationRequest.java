package co.uk.stirling_index.inventory.model.DTO;


import co.uk.stirling_index.inventory.model.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductCreationRequest {

    private String name;
    private String category;
    private Integer quantity;
    private Long price;
    private String imageURI;

    public boolean hasAllRequiredFields() {
        return name != null && category != null && quantity != null && price != null;
    }
}
