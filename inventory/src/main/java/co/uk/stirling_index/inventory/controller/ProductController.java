package co.uk.stirling_index.inventory.controller;

import co.uk.stirling_index.inventory.model.product.dto.ProductCreationRequest;
import co.uk.stirling_index.inventory.model.product.dto.ProductDetailUpdate;
import co.uk.stirling_index.inventory.model.product.Product;
import co.uk.stirling_index.inventory.model.security.userdetails.AuthenticatedUser;
import co.uk.stirling_index.inventory.service.assemblers.ProductAssembler;
import co.uk.stirling_index.inventory.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"api/products", "api/products/"})
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
    public ResponseEntity<EntityModel<Product>> getProduct(@PathVariable UUID productID) {
        Product product = productService.getProductById(productID);

        return new ResponseEntity<>(productAssembler.toModel(product), HttpStatus.OK);
    }

    @GetMapping("business/{businessId}")
    ResponseEntity<CollectionModel<EntityModel<Product>>> getAllProductsFromBusiness(@PathVariable UUID businessId) {
        List<Product> products = productService.getAllProducts(businessId);

        return new ResponseEntity<>(productAssembler.toCollectionModel(products), HttpStatus.OK);
    }

    /*
        SECURE ENDPOINTS - WRITE / UPDATE / DELETE
     */

    /**
     * Add a product to a business.
     * @param request - a request containing the product details in JSON format as RequestBody
     * @return The newly created product along with HATEOAS links.
     */
    // TODO Require correct business credentials + OAuth ?
    @PostMapping("/business/{businessID}")
    @PreAuthorize("hasRole('BUSINESS') or hasRole('OPERATOR')")
    public ResponseEntity<EntityModel<Product>> addProduct
    (
            @RequestBody ProductCreationRequest request,
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID businessID
    )
    {
        Product saved = productService.addProduct(request, user, businessID);
        logger.info("Business with ID: {}  ADDED Product with ID: {} ",
                saved.getBusiness().getId(),
                saved.getId()
        );
        return new ResponseEntity<>(productAssembler.toModel(saved), HttpStatus.CREATED);
    }

    @PutMapping("/{productID}")
    @PreAuthorize("hasRole('BUSINESS') or hasRole('OPERATOR')")
    public ResponseEntity<EntityModel<Product>> updateProduct
            (
            @RequestBody ProductDetailUpdate request,
            @PathVariable UUID productID,
            @AuthenticationPrincipal AuthenticatedUser user
            )
    {
        Product saved = productService.updateProduct(request, user, productID);
        logger.info("Business with ID: {}  UPDATED Product with ID: {} ",
                saved.getBusiness().getId(),
                saved.getId()
        );
        return new ResponseEntity<>(productAssembler.toModel(saved), HttpStatus.OK);
    }


    @DeleteMapping("/{productID}")
    @PreAuthorize("hasRole('BUSINESS') or hasRole('OPERATOR')")
    public ResponseEntity<?> deleteProduct
            (
            @PathVariable UUID productID,
            @AuthenticationPrincipal AuthenticatedUser user
            )
    {
        // TODO Perhaps this function could return either 204, no content, or link back to the products page for the business?
        productService.deleteProduct(productID, user);
        logger.info("Business with ID: {}  DELETED Product with ID: {} ", user.businessID(), productID);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
