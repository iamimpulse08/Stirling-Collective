package co.uk.stirling_index.inventory.service;

import co.uk.stirling_index.inventory.model.DTO.ProductCreationRequest;
import co.uk.stirling_index.inventory.model.Product;
import co.uk.stirling_index.inventory.service.assemblers.ProductAssembler;
import co.uk.stirling_index.inventory.service.repository.ProductsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    private final ProductsRepository productsRepository;
    private final BusinessService businessService;

    public ProductService(ProductsRepository productsRepository, BusinessService businessService, ProductAssembler productAssembler) {
        this.productsRepository = productsRepository;
        this.businessService = businessService;
    }

    public boolean isProductIdValid(Product product) {
        if (product == null || product.getId() < 0) {
            return false;
        }

        return !productsRepository.existsById(product.getId());
    }

    public Product addProduct(ProductCreationRequest request, Integer businessId) {
        // if the product is null or id is invalid, return
        if (request == null || businessId == null || businessId < 0) {
            throw new IllegalArgumentException("Product is null, or ID is invalid" + request);
        }
        else if (!request.hasAllRequiredFields()) {
            throw new IllegalArgumentException("Product is missing required fields" + request);
        }
        else if (request.getPrice() < 0) {
            throw new IllegalArgumentException("Product price is invalid" + request);
        }
        else if (request.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity is invalid" + request);
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setBusiness_id(businessId);
        product.setCategory(request.getCategory());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());

        return productsRepository.save(product);
    }

    public Product updateProduct(Product product, Long businessId) {
        if (isProductIdValid(product)) {
            throw new IllegalArgumentException("Product is null, or ID is invalid" + product);
        }

        // TODO Attribute parsing, what's being updated, what's allowed, etc.

        return productsRepository.save(product);
    }

    public void deleteProduct(Product product) {
        if (isProductIdValid(product)) {
            throw new IllegalArgumentException("Product is null, or ID is invalid" + product);
        }
        productsRepository.delete(product);
    }

    public void deleteProductById(Integer id, Long businessId) {

        if (id == null || id < 0) {
            throw new IllegalArgumentException("ID is invalid" + id);
        }

        productsRepository.deleteById(id);
    }

    public Product getProductById(Integer id) {
        return productsRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Product with ID " + id + " not found"));
    }

    public List<Product> getAllProducts() {
        return productsRepository.findAll();
    }

    public List<Product> getAllProducts(Integer businessId) {
        return productsRepository.findAllByBusiness_id(businessId);
    }
}
