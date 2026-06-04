package co.uk.stirling_index.inventory.service.repository;

import co.uk.stirling_index.inventory.model.Product;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends CrudRepository<Product, Integer> {

    Product findProductById(Integer id);


    List<Product> findProductsByBusinessId(Long businessId);

}
