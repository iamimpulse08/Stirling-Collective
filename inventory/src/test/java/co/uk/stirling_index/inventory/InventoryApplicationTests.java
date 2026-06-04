package co.uk.stirling_index.inventory;

import co.uk.stirling_index.inventory.model.Business;
import co.uk.stirling_index.inventory.model.Product;
import co.uk.stirling_index.inventory.service.repository.BusinessRepository;
import co.uk.stirling_index.inventory.service.repository.ProductsRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class InventoryApplicationTests {

	@BeforeEach
	void setup() {
		Business business1 = new Business("Business A", "123 Street", "FK8 2EE", "123@gmail.com", "01234567890");
		Business business2 = new Business("Business B", "456 Street", "FK8 2EE", "456@gmail.com", "07924823491");

		// save to theoretical postgres database
		business1 = businessRepository.save(business1);
		business2 = businessRepository.save(business2);


		System.out.println("BUSINESS 1 ID = :::::" + business1.getBusiness_id());

		Product product1 = new Product("milk", business1.getBusiness_id(), "food", 10, 2.99, "abc");
		Product product2 = new Product("bread", business1.getBusiness_id(), "food", 15, 3.99, "abc");

		product1 = productsRepository.save(product1);
		product2 = productsRepository.save(product2);
	}

	@BeforeAll
	static void setupContainer() {
		postgreSQLContainer.start();
		System.out.println("CONTAINER STARTED" + " USER & PASS" + postgreSQLContainer.getUsername() + " " + postgreSQLContainer.getPassword());;
	}

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:18.0");

	@Autowired
	BusinessRepository businessRepository;
	@Autowired
	ProductsRepository productsRepository;

	@Test
	void connectionEstablished() {
		assert postgreSQLContainer.isRunning();
		assert postgreSQLContainer.isCreated();
	}

	@Test
	void addProduct() {

	}

	@Test
	void updateProduct() {

	}

	@Test
	void deleteProduct() {

	}

}
