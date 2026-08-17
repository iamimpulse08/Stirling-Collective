package co.uk.stirling_index.inventory.service.repository;

import co.uk.stirling_index.inventory.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductsRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findProductById(Integer id);

    @Query("SELECT p FROM Product p WHERE p.business.id = ?1")
    List<Product> findAllByBusiness_id(UUID businessId);

    @Query("SELECT p FROM Product p WHERE p.id = ?1 AND p.business.id = ?2")
    Optional<Product> findByProductIdAndBusinessId(UUID productId, UUID businessId);

    @Query("DELETE FROM Product p WHERE p.id = ?1 AND p.business.id = ?2")
    Long deleteByProductIdAndBusinessId(Integer productId, UUID businessId);
}
