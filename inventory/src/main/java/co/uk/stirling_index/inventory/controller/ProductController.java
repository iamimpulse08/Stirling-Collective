package co.uk.stirling_index.inventory.controller;

import co.uk.stirling_index.inventory.model.DTO.ProductCreationRequest;
import co.uk.stirling_index.inventory.model.Product;
import co.uk.stirling_index.inventory.service.assemblers.ProductAssembler;
import co.uk.stirling_index.inventory.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("api/products/")
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final ProductAssembler productAssembler;

    public ProductController(ProductService productService, ProductAssembler productAssembler) {
        this.productService = productService;
        this.productAssembler = productAssembler;
    }


    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        CollectionModel<EntityModel<Product>> collectionModel = productAssembler.toCollectionModel(products);

        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    }

    @GetMapping("{productID}")
    public EntityModel<Product> getProduct(@PathVariable Integer productID) {
        return null;
    }

    @GetMapping("business/{businessId}")
    CollectionModel<EntityModel<Product>> getAllProductsFromBusiness(@PathVariable int businessId) {
        return null;
    }

    /**
     * Authorised actions
     */

    // TODO Require correct business credentials + OAuth ?
    @PostMapping("business/{businessId}")
    public ResponseEntity<EntityModel<Product>> addProduct(
            @PathVariable int businessId,
            @RequestBody ProductCreationRequest request) {
        Product saved = productService.addProduct(request, businessId);
        logger.info("Business with ID: {}  ADDED Product with ID: {} ", businessId, saved.getId());
        return new ResponseEntity<>(productAssembler.toModel(saved), HttpStatus.CREATED);
    }

    @PutMapping("business/{businessId}/{productID}")
    @ResponseStatus(value = org.springframework.http.HttpStatus.OK)
    public EntityModel<Product> updateProduct(@PathVariable Long businessId, @RequestBody Product product) {
        Product saved = productService.updateProduct(product, businessId);
        logger.info("Business with ID: {}  UPDATED Product with ID: {} ", businessId, product.getId());
        return productAssembler.toModel(saved);
    }


    @DeleteMapping("business/{businessId}/{productID}")
    @ResponseStatus(value = org.springframework.http.HttpStatus.OK)
    public void deleteProduct(@PathVariable Long businessId, @PathVariable String productID) {
        // TODO Perhaps this function could return either 204, no content, or link back to the products page for the business?
        productService.deleteProductById(Integer.parseInt(productID), businessId);
        logger.info("Business with ID: {}  DELETED Product with ID: {} ", businessId, productID);
    }
}
