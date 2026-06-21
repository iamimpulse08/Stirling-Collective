package co.uk.stirling_index.inventory.model.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDetailUpdate {

    private String name;
    private String category;
    private Integer quantity;
    private Long price;
    private String imageURI;
}
