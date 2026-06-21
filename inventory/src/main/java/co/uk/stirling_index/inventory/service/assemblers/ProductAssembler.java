package co.uk.stirling_index.inventory.service.assemblers;


import co.uk.stirling_index.inventory.controller.ProductController;
import co.uk.stirling_index.inventory.model.Product;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductAssembler implements RepresentationModelAssembler<Product, EntityModel<Product>> {


    @Override
    public EntityModel<Product> toModel(Product product) {

        EntityModel<Product> entityModel = EntityModel.of(product,
                linkTo(methodOn(ProductController.class).getProduct(product.getId())).withRel("product")
        );

        return entityModel;
    }

    /**
     * For all authorised users, add the following links to the entityModel.
     * @param entityModel - the entityModel to add the links to.
     */
    private void authorisationLinks(EntityModel<Product> entityModel) {
    }

    @Override
    @NonNull
    public CollectionModel<EntityModel<Product>> toCollectionModel(@NonNull Iterable<? extends Product> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
