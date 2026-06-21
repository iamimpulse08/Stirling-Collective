package co.uk.stirling_index.inventory;

import co.uk.stirling_index.inventory.model.Business;
import co.uk.stirling_index.inventory.model.DTO.ProductCreationRequest;
import co.uk.stirling_index.inventory.model.DTO.ProductDetailUpdate;
import co.uk.stirling_index.inventory.model.Product;
import co.uk.stirling_index.inventory.service.repository.BusinessRepository;
import co.uk.stirling_index.inventory.service.repository.ProductsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryProductControllerTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:18");

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


	/**
	 * Checks that the connection to the database is established.
	 */
	@Test
	void connectionEstablished() {
		assertTrue(postgreSQLContainer.isCreated());
		assertTrue(postgreSQLContainer.isRunning());
	}

	/**
	 * Setup method to initialise the database before each test.
	 *
	 * This method will delete all data from the database before each test.
	 *
	 * This method binds the rest test client to the local server, and the port of the application.
	 *
	 * This method will initialise the database with some test data (2 products, 2 businesses).
	 *
	 */
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

		Product product1 = new Product("milk", business1.getBusiness_id(), "food", 10, 299L, "abc");
		Product product2 = new Product("bread", business2.getBusiness_id(), "food", 15, 399L, "abc");

        productsRepository.save(product1);
        productsRepository.save(product2);
    }


	/**
	 * A test for adding products, where all mandatory fields are present.
	 */
	@Test
	void addProductWithAllMandatoryDetails() {
		ProductCreationRequest productCreationRequest = new ProductCreationRequest();
		productCreationRequest.setName("milk");
		productCreationRequest.setCategory("food");
		productCreationRequest.setQuantity(10);
		productCreationRequest.setPrice(299L);

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

	/**
	 * A test for adding products, where it is missing at least one mandatory field.
	 */
	@Test
	void addProductWithMissingMandatoryDetails() {
		ProductCreationRequest productCreationRequest = new ProductCreationRequest();
		productCreationRequest.setName("milk");
		productCreationRequest.setCategory("food");
		productCreationRequest.setQuantity(10);

		// Missing price post-request, expects 400 Bad Request
		restTestClient.post()
				.uri("/api/products/business/" + businessRepository.findAll().getFirst().getBusiness_id())
				.body(productCreationRequest)
				.exchange()
				.expectStatus().isBadRequest();
	}

	/**
	 * A test for adding products, where the business id is invalid.
	 */
	@Test
	void addProductWithInvalidBusinessId() {
		ProductCreationRequest productCreationRequest = new ProductCreationRequest();
		productCreationRequest.setName("milk");
		productCreationRequest.setCategory("food");
		productCreationRequest.setQuantity(10);
		productCreationRequest.setPrice(299L);

		restTestClient.post()
				.uri("/api/products/business/1234567890")
				.body(productCreationRequest)
				.exchange()
				.expectStatus()
				.isNotFound();
	}

	/**
	 * A test for retrieving a product from a specific business from the api/products/business/{business_id} root.
	 */
	@Test
	void retrieveProductFromBusiness() {
		var responseBody = restTestClient.get()
				.uri("/api/products/business/" + businessRepository.findAll().getFirst().getBusiness_id())
				.exchange()
				.expectStatus().isOk()
				.expectHeader()
				.contentTypeCompatibleWith("application/hal+json")
				.expectBody()
				.jsonPath("$._embedded.productList.length()").isEqualTo(1);


		assertNotNull(responseBody);
		System.out.println(responseBody.json(""));
	}

	/**
	 * A test for retrieving all products from all businesses from the api/products/ root.
	 */
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

	/**
	 * A test for retrieving a product by id from the api/products/{id} end-point.
	 */
	@Test
	void retrieveProductById() {
		Product product = productsRepository.findAll().getFirst();

		var responseBody = restTestClient.get()
				.uri("/api/products/" + product.getId())
				.exchange()
				.expectStatus().isOk()
				.expectHeader()
				.contentTypeCompatibleWith("application/hal+json")
				.expectBody()
				.jsonPath("$.id").isEqualTo(product.getId());

		assertNotNull(responseBody);
	}

	/**
	 * A test for retrieving a product by id from the api/products/{id} end-point, where the id is invalid.
	 */
	@Test
	void retrieveProductByIdWithInvalidId() {
		var responseBody = restTestClient.get()
				.uri("/api/products/1234567890")
				.exchange()
				.expectStatus()
				.isNotFound();
		assertNotNull(responseBody);
	}

	/**
	 * A test for updating a product with varying differing attributes.
	 */
	@Test
	 void updateProduct() {
		Product product = productsRepository.findAll().getFirst();
		Integer businessId = businessRepository.findAll().getFirst().getBusiness_id();

		ProductDetailUpdate detailUpdate = new ProductDetailUpdate();
		detailUpdate.setName("milk");
		detailUpdate.setCategory("food");
		detailUpdate.setQuantity(25);
		detailUpdate.setPrice(1400L);

		restTestClient.put()
				.uri("/api/products/business/" + businessId + "/" + product.getId())
				.body(detailUpdate)
				.exchange()
				.expectStatus().isOk();
	}

	/**
	 * A test for updating a product with an invalid PRODUCT id.
	 */
	@Test
	void updateProductWithInvalidId() {
		ProductDetailUpdate detailUpdate = new ProductDetailUpdate();
		detailUpdate.setName("milk");
		detailUpdate.setCategory("food");
		detailUpdate.setQuantity(25);
		detailUpdate.setPrice(1400L);

		Integer businessId = businessRepository.findAll().getFirst().getBusiness_id();

		restTestClient.put()
				.uri("/api/products/business/" + businessId + "/1234567890")
				.body(detailUpdate)
				.exchange()
				.expectStatus().isNotFound();
	}

	/**
	 * A test for updating a product with an invalid BUSINESS id.
	 */
	@Test
	void updateProductWithInvalidBusinessId() {
		Product product = productsRepository.findAll().getFirst();
		Integer productId = product.getId();

		ProductDetailUpdate detailUpdate = new ProductDetailUpdate();
		detailUpdate.setName("milk");
		detailUpdate.setCategory("food");
		detailUpdate.setQuantity(25);
		detailUpdate.setPrice(1400L);

		restTestClient.put()
				.uri("/api/products/business/1234567890/" + productId)
				.body(detailUpdate)
				.exchange()
				.expectStatus().isNotFound();
	}

	/**
	 * A test for updating a product with a negative price.
	 */
	@Test
	void updateProductWithInvalidPrice() {
		Product product = productsRepository.findAll().getFirst();
		Integer businessId = businessRepository.findAll().getFirst().getBusiness_id();
		Integer productId = product.getId();

		ProductDetailUpdate detailUpdate = new ProductDetailUpdate();
		detailUpdate.setPrice(-199L);
		detailUpdate.setQuantity(product.getQuantity());
		detailUpdate.setCategory(product.getCategory());
		detailUpdate.setName(product.getName());


		restTestClient.put()
				.uri("/api/products/business/" + businessId + "/" + productId)
				.body(detailUpdate)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void deleteProduct() {

	}

}
