package co.uk.stirling_index.inventory.service.repository;

import co.uk.stirling_index.inventory.model.Product;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Integer> {

    Product findProductById(Integer id);


    @Query("SELECT * FROM products WHERE business_id = :business_id")
    List<Product> findProductsByBusiness_id(Long business_id);

    List<Product> findAllProductsByBusiness_name(String business_name);

}
