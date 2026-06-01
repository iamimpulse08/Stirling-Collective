package co.uk.stirling_index.inventory.controller;

import co.uk.stirling_index.inventory.model.Product;
import co.uk.stirling_index.inventory.service.assemblers.ProductAssembler;
import co.uk.stirling_index.inventory.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/products")
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final ProductAssembler productAssembler;

    public ProductController(ProductService productService, ProductAssembler productAssembler) {
        this.productService = productService;
        this.productAssembler = productAssembler;
    }


    @GetMapping
    public CollectionModel<EntityModel<Product>> getAllProducts() {
        List<Product> allProducts = new ArrayList<>();
        return null;
        // CollectionModel.of(products); # (, linkTo(methodOn(Products.class).getProduct(0)).withSelfRel())
    }

    @GetMapping("{productID}")
    public EntityModel<Product> getProduct(@PathVariable String productID) {
        return null;
    }

    @GetMapping("business/{businessId}")
    EntityModel<Product> getAllProductsFromBusiness(@PathVariable Long businessId) {
        List<Product> products = productService.getAllProductsByBusinessId(businessId);
        return null;
    }

    /**
     * Authorised actions
     */

    // TODO Require correct business credentials + OAuth ?
    @PostMapping("business/{businessId}")
    @ResponseStatus(value = org.springframework.http.HttpStatus.CREATED)
    public EntityModel<Product> addProduct(@PathVariable Long businessId, @RequestBody Product product) {
        Product saved = productService.addProduct(product, businessId);
        logger.info("Business with ID: {}  ADDED Product with ID: {} ", businessId, product.getId());
        return productAssembler.toModel(saved);
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
