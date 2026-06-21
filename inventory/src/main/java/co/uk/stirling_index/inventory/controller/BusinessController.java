package co.uk.stirling_index.inventory.controller;

import co.uk.stirling_index.inventory.model.Product;
import co.uk.stirling_index.inventory.service.BusinessService;
import co.uk.stirling_index.inventory.service.ProductService;
import co.uk.stirling_index.inventory.service.assemblers.ProductAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/businesses")
// TODO Security of businessId == authenticated user email, businessid, username, password match (?)
public class BusinessController {

    private final BusinessService businessService;
    private final ProductService productService;

    private final ProductAssembler productAssembler;

    private final Logger logger = LoggerFactory.getLogger(BusinessController.class);

    BusinessController(BusinessService businessService,
                       ProductService productService,
                       ProductAssembler productAssembler) {
        this.businessService = businessService;
        this.productService = productService;
        this.productAssembler = productAssembler;
    }

    /**
     * Businesses
     */
    @PostMapping()
    public void addBusiness() {

    }

    @PutMapping("{businessID}")
    public void updateBusiness(@PathVariable String businessID) {
    }

    @DeleteMapping("{businessID}")
    public void deleteBusiness(@PathVariable String businessID) {
    }
}

