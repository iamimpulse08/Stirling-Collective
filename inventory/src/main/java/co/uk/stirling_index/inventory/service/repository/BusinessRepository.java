package co.uk.stirling_index.inventory.service.repository;

import co.uk.stirling_index.inventory.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

@Repository
public interface BusinessRepository extends JpaRepository<Business, UUID> {

    Business findBusinessByEmail(String email);
}
