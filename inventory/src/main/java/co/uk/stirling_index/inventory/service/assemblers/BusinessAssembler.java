package co.uk.stirling_index.inventory.service.assemblers;

import co.uk.stirling_index.inventory.model.Business;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class BusinessAssembler implements RepresentationModelAssembler<Business, EntityModel<Business>> {
    @Override
    public EntityModel<Business> toModel(Business entity) {
        return null;
    }

    @Override
    public CollectionModel<EntityModel<Business>> toCollectionModel(Iterable<? extends Business> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
