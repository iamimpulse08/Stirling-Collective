package co.uk.stirling_index.inventory;

import co.uk.stirling_index.inventory.controller.ProductController;
import co.uk.stirling_index.inventory.model.Business;
import co.uk.stirling_index.inventory.model.DTO.ProductCreationRequest;
import co.uk.stirling_index.inventory.model.Product;
import co.uk.stirling_index.inventory.service.ProductService;
import co.uk.stirling_index.inventory.service.assemblers.ProductAssembler;
import co.uk.stirling_index.inventory.service.repository.BusinessRepository;
import co.uk.stirling_index.inventory.service.repository.ProductsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryProductControllerTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.4");

	/**
	 * Autowired Repositories
	 */
	@Autowired
	BusinessRepository businessRepository;
	@Autowired
	ProductsRepository productsRepository;

	/**
	 * Rest Test Client
	 */
	RestTestClient restTestClient;

	@LocalServerPort
	int port;

	/**
	 * Local Test Variables
	 */

	final int noOfProducts = 2;
	final int noOfBusinesses = 2;


	@Test
	void connectionEstablished() {
		assert postgreSQLContainer.isRunning();
	}

	@BeforeEach
	void setup() {

		productsRepository.deleteAll();
		businessRepository.deleteAll();

		restTestClient = RestTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.build();

		initialiseDB();
	}

	void initialiseDB() {
		// TODO Could probably create a generic generation strategy using fixed size final variables,
		//  then re-using the collection of object(s) to compare against data stored in the Postgres DB.
		Business business1 = new Business("Business A", "123 Street", "FK8 2EE", "123@gmail.com", "01234567890");
		Business business2 = new Business("Business B", "456 Street", "FK8 2EE", "456@gmail.com", "07924823491");

		// save to theoretical postgres database
		business1 = businessRepository.save(business1);
		business2 = businessRepository.save(business2);

		Product product1 = new Product("milk", business1.getBusiness_id(), "food", 10, 299, "abc");
		Product product2 = new Product("bread", business2.getBusiness_id(), "food", 15, 399, "abc");

        productsRepository.save(product1);
        productsRepository.save(product2);
    }


	@Test
	void addProduct() {
		ProductCreationRequest productCreationRequest = new ProductCreationRequest();
		productCreationRequest.setName("milk");
		productCreationRequest.setCategory("food");
		productCreationRequest.setQuantity(10);
		productCreationRequest.setPrice(299);

		var responseBody = restTestClient.post()
				.uri("/api/products/business/" + businessRepository.findAll().getFirst().getBusiness_id())
				.body(productCreationRequest)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader()
				.contentTypeCompatibleWith("application/hal+json")
				.expectBody()
				.jsonPath("$.name").isEqualTo("milk");

		assertNotNull(responseBody);
		}


	@Test
	void retrieveAllProducts() {
		var responseBody = restTestClient.get()
				.uri("/api/products/")
				.exchange()
				.expectStatus().isOk()
				.expectHeader()
				.contentTypeCompatibleWith("application/hal+json")
				.expectBody()
				.jsonPath("$._embedded.productList.length()").isEqualTo(2);

		assertNotNull(responseBody);
	}

	@Test
	void updateProduct() {

	}

	@Test
	void deleteProduct() {

	}

}
