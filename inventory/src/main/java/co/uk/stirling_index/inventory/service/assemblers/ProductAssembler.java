package co.uk.stirling_index.inventory.service.assemblers;


import co.uk.stirling_index.inventory.controller.ProductController;
import co.uk.stirling_index.inventory.model.Product;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.core.EmbeddedWrapper;
import org.springframework.hateoas.server.core.EmbeddedWrappers;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductAssembler implements RepresentationModelAssembler<Product, EntityModel<Product>> {

    private static final EmbeddedWrappers WRAPPERS = new EmbeddedWrappers(false);

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
        List<EntityModel<Product>> productModels = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .toList();
        return CollectionModel.of(productModels, linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
    }
}
