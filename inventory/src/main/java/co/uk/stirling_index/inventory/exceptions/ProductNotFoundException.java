package co.uk.stirling_index.inventory.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Integer productId) {
        super("Product with id " + productId + " not found.");
    }
}
