package co.uk.stirling_index.inventory.model.DTO;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductCreationRequest {

    private String name;
    private String category;
    private int quantity;
    private long price;
    private String imageURI;
}
