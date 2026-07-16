package co.uk.stirling_index.inventory.exceptions;

public class InvalidProductUpdateException extends RuntimeException {
    public InvalidProductUpdateException(String message) {
        super(message);
    }
}
