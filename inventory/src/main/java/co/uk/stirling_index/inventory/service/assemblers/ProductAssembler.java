package co.uk.stirling_index.inventory.service.assemblers;


import co.uk.stirling_index.inventory.model.Product;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ProductAssembler implements RepresentationModelAssembler<Product, EntityModel<Product>> {


    @Override
    public EntityModel<Product> toModel(Product entity) {
        return null;
    }

    @Override
    public CollectionModel<EntityModel<Product>> toCollectionModel(Iterable<? extends Product> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
