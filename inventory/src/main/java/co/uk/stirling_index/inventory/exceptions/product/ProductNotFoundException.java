package co.uk.stirling_index.inventory.exceptions.product;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID productId) {
        super("Product with id " + productId + " not found.");
    }
}
