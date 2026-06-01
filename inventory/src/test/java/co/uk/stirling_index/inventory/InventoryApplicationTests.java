package co.uk.stirling_index.inventory;

import co.uk.stirling_index.inventory.model.Product;
import co.uk.stirling_index.inventory.service.repository.ProductsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@SpringBootTest
@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InventoryApplicationTests {

	@BeforeEach
	void setup() {
		List<Product> products = List.of(
				new Product(0, "general", 5, 4.99),
				new Product(1, "food", 15, 4.20)
		);

		// save to theoretical postgres database
		productsRepository.saveAll(products);
	}

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0");

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
