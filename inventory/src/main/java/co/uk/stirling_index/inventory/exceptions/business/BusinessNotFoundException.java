package co.uk.stirling_index.inventory.exceptions.business;

import java.util.UUID;

public class BusinessNotFoundException extends RuntimeException {
    public BusinessNotFoundException(UUID businessId) {
        super("Business with id " + businessId + " not found.");
    }
}
