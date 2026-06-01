package co.uk.stirling_index.inventory.service;

import co.uk.stirling_index.inventory.model.Product;
import co.uk.stirling_index.inventory.service.repository.ProductsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductsRepository productsRepository;
    private final BusinessService businessService;

    public ProductService(ProductsRepository productsRepository, BusinessService businessService) {
        this.productsRepository = productsRepository;
        this.businessService = businessService;
    }

    public boolean isProductIdValid(Product product) {
        if (product == null || product.getId() < 0) {
            return false;
        }

        return !productsRepository.existsById(product.getId());
    }

    public Product addProduct(Product product, Long businessId) {
        // if the product is null or id is invalid, return
        if (!isProductIdValid(product)) {
            throw new IllegalArgumentException("Product is null, or ID is invalid" + product);
        }

        return productsRepository.save(product);
    }

    public Product updateProduct(Product product, Long businessId) {
        if (!isProductIdValid(product)) {
            throw new IllegalArgumentException("Product is null, or ID is invalid" + product);
        }

        // TODO Attribute parsing, what's being updated, what's allowed, etc.

        return productsRepository.save(product);
    }

    public void deleteProduct(Product product) {
        if (!isProductIdValid(product)) {
            throw new IllegalArgumentException("Product is null, or ID is invalid" + product);
        }
        productsRepository.delete(product);
    }

    public void deleteProductById(int id, Long businessId) {

        if (id < 0) {
            throw new IllegalArgumentException("ID is invalid" + id);
        }

        productsRepository.deleteById(id);
    }

    public void getAllProducts() {
        productsRepository.findAll();
    }

    public List<Product> getAllProductsByBusinessId(Long businessId) {
        // custom query where business_id = businessId

        return productsRepository.findProductsByBusinessId(businessId);
    }
}
