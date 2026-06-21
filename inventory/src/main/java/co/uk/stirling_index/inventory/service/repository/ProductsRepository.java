package co.uk.stirling_index.inventory.service.repository;

import co.uk.stirling_index.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Integer> {

    Product findProductById(Integer id);

    @Query("SELECT p FROM Product p WHERE p.business_id = ?1")
    List<Product> findAllByBusiness_id(Integer businessId);
}
