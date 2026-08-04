package co.uk.stirling_index.inventory.service;

import co.uk.stirling_index.inventory.exceptions.business.BusinessNotFoundException;
import co.uk.stirling_index.inventory.exceptions.product.InvalidProductUpdateException;
import co.uk.stirling_index.inventory.exceptions.product.ProductNotFoundException;
import co.uk.stirling_index.inventory.model.Business;
import co.uk.stirling_index.inventory.model.DTO.product.ProductCreationRequest;
import co.uk.stirling_index.inventory.model.DTO.product.ProductDetailUpdate;
import co.uk.stirling_index.inventory.model.Product;
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

    public ProductService(ProductsRepository productsRepository, BusinessService businessService, ProductAssembler productAssembler) {
        this.productsRepository = productsRepository;
        this.businessService = businessService;
    }

    public Product addProduct(ProductCreationRequest request, UUID businessId) {
        // if the product is null or id is invalid, return
        if (businessId == null) {
            throw new BusinessNotFoundException(businessId);
        }
        else if (request == null || !request.hasAllRequiredFields()) {
            throw new InvalidProductUpdateException("Product is missing required fields" + request);
        }
        else if (request.getPrice() < 0) {
            throw new IllegalArgumentException("Product price is invalid" + request);
        }
        else if (request.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity is invalid" + request);
        }

        Business business = businessService.getBusinessById(businessId);

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

    public Product updateProduct(ProductDetailUpdate productUpdateRequest, UUID businessId) {
        // TODO Attribute parsing, what's being updated, what's allowed, etc.

        if (productUpdateRequest == null || businessId == null) {
            throw new BusinessNotFoundException(businessId);
        }

        if (productUpdateRequest.getId() == null) {
            throw new ProductNotFoundException(productUpdateRequest.getId());
        }

        if (!isValidPrice(productUpdateRequest.getPrice())) {
            String message = String.format("Product price is invalid: %d", productUpdateRequest.getPrice());
            throw new IllegalArgumentException(message);
        }

        Product toUpdate = productsRepository.findByProductIdAndBusinessId(productUpdateRequest.getId(), businessId)
                .orElseThrow(
                () -> new ProductNotFoundException(productUpdateRequest.getId())
        );

        toUpdate.setName(productUpdateRequest.getName());
        toUpdate.setCategory(productUpdateRequest.getCategory());
        toUpdate.setQuantity(productUpdateRequest.getQuantity());
        toUpdate.setPrice(productUpdateRequest.getPrice());

        return productsRepository.save(toUpdate);
    }

    /**
     * Deletion operation for a product.
     * @param productId - the product to delete
     * @param businessId - the business that owns the product
     */
    public void deleteProduct(UUID productId, UUID businessId) {

        Product fromRepo = productsRepository.findByProductIdAndBusinessId(productId, businessId)
                .orElseThrow(
                        () -> new ProductNotFoundException(productId)
                );

        productsRepository.delete(fromRepo);
    }

    public Product getProductById(UUID id) {
        Optional<Product> product = productsRepository.findById(id);

        return product.orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> getAllProducts() {
        return productsRepository.findAll();
    }

    public List<Product> getAllProducts(Integer businessId) {
        return productsRepository.findAllByBusiness_id(businessId);
    }
}
