package co.uk.stirling_index.inventory.service;

import co.uk.stirling_index.inventory.model.product.Product;
import co.uk.stirling_index.inventory.model.security.userdetails.AuthenticatedUser;
import co.uk.stirling_index.inventory.model.security.Role;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnershipValidator {

    public void assertCanModify(Product product, AuthenticatedUser user) {

        if (user.role() == Role.OPERATOR) return;

        if (user.role() != Role.BUSINESS) {
            String message = String.format("Role: %s is cannot modify products.", user.email());
            throw new AccessDeniedException(message);
        }

        UUID resourceBusinessId = product.getBusiness().getId();

        if (user.businessID() == null || !user.businessID().equals(resourceBusinessId)) {
            String message = String.format("Business ID: %s is not the owner of the resource.", user.businessID());
            throw new AccessDeniedException(message);
        }
    }

    public void assertCanCreateFor(UUID targetBusinessID, AuthenticatedUser user) {
        if (user.role() == Role.OPERATOR) return;

        if (user.role() != Role.BUSINESS) {
            String message = String.format("Role: %s is cannot create products.", user.email());
            throw new AccessDeniedException(message);
        }
        if (user.businessID() == null || !user.businessID().equals(targetBusinessID)) {
            String message = String.format("Business ID: %s is not the owner of the resource.", user.businessID());
            throw new AccessDeniedException(message);
        }
    }
}
