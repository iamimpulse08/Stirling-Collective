package co.uk.stirling_index.inventory.service;

import co.uk.stirling_index.inventory.exceptions.product.InvalidProductUpdateException;
import co.uk.stirling_index.inventory.exceptions.product.ProductNotFoundException;
import co.uk.stirling_index.inventory.model.business.Business;
import co.uk.stirling_index.inventory.model.product.dto.ProductCreationRequest;
import co.uk.stirling_index.inventory.model.product.dto.ProductDetailUpdate;
import co.uk.stirling_index.inventory.model.product.Product;
import co.uk.stirling_index.inventory.model.security.userdetails.AuthenticatedUser;
import co.uk.stirling_index.inventory.service.assemblers.ProductAssembler;
import co.uk.stirling_index.inventory.service.repository.ProductsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductsRepository productsRepository;
    private final BusinessService businessService;
    private final OwnershipValidator ownershipValidator;

    public ProductService(ProductsRepository productsRepository, BusinessService businessService, ProductAssembler productAssembler, OwnershipValidator ownershipValidator) {
        this.productsRepository = productsRepository;
        this.businessService = businessService;
        this.ownershipValidator = ownershipValidator;
    }

    public Product addProduct(ProductCreationRequest request, AuthenticatedUser user, UUID businessID) {
        // if the product is null or id is invalid, return
        if (request == null || !request.hasAllRequiredFields()) {
            throw new InvalidProductUpdateException("Product is missing required fields" + request);
        }
        else if (request.getPrice() < 0) {
            throw new IllegalArgumentException("Product price is invalid" + request);
        }
        else if (request.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity is invalid" + request);
        }

        // The business ID is set via the method parameter, not by the user as this could introduce NPE bugs when the user is operator.
        // TODO An alternative solution to this would be to assign a business to the operator, which is not null.
        ownershipValidator.assertCanCreateFor(businessID, user);
        Business business = businessService.getBusinessById(businessID);

        Product product = new Product();
        product.setName(request.getName());
        product.setBusiness(business);
        product.setCategory(request.getCategory());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());

        return productsRepository.save(product);
    }

    public boolean isValidPrice(Long price) {
        return price >= 0;
    }

    public Product updateProduct(ProductDetailUpdate productUpdateRequest, AuthenticatedUser user, UUID productID) {
        // TODO Attribute parsing, what's being updated, what's allowed, etc.

        // TODO fix passing null into this method - could pass in a product ID, or a product object from the controller/request.
        if (productID == null) {
            throw new ProductNotFoundException(null);
        }

        if (!isValidPrice(productUpdateRequest.getPrice())) {
            String message = String.format("Product price is invalid: %d", productUpdateRequest.getPrice());
            throw new IllegalArgumentException(message);
        }

        Product toUpdate = productsRepository.findById(productID)
                .orElseThrow(() -> new ProductNotFoundException(productID)
        );
        ownershipValidator.assertCanModify(toUpdate, user);

        toUpdate.setName(productUpdateRequest.getName());
        toUpdate.setCategory(productUpdateRequest.getCategory());
        toUpdate.setQuantity(productUpdateRequest.getQuantity());
        toUpdate.setPrice(productUpdateRequest.getPrice());

        return productsRepository.save(toUpdate);
    }

    /**
     * Deletion operation for a product.
     *
     * @param productId - the product to delete
     * @param user
     */
    public void deleteProduct(UUID productId, AuthenticatedUser user) {

        Product product = productsRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException(productId));

        ownershipValidator.assertCanModify(product, user);
        productsRepository.delete(product);
    }

    public Product getProductById(UUID id) {
        Optional<Product> product = productsRepository.findById(id);

        return product.orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> getAllProducts() {
        return productsRepository.findAll();
    }

    public List<Product> getAllProducts(UUID businessId) {
        return productsRepository.findAllByBusiness_id(businessId);
    }
}
