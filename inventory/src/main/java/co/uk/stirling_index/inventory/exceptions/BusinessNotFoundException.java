package co.uk.stirling_index.inventory.exceptions;

public class BusinessNotFoundException extends RuntimeException {
    public BusinessNotFoundException(Integer businessId) {
        super("Business with id " + businessId + " not found.");
    }
}
