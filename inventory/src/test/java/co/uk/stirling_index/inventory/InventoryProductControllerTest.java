package co.uk.stirling_index.inventory;

import co.uk.stirling_index.inventory.model.security.AccessCase;
import co.uk.stirling_index.inventory.model.business.Business;
import co.uk.stirling_index.inventory.model.product.dto.ProductCreationRequest;
import co.uk.stirling_index.inventory.model.product.dto.ProductDetailUpdate;
import co.uk.stirling_index.inventory.model.product.Product;
import co.uk.stirling_index.inventory.model.security.Role;
import co.uk.stirling_index.inventory.service.repository.BusinessRepository;
import co.uk.stirling_index.inventory.service.repository.ProductsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InventoryProductControllerTest extends IntegrationTest {
	/**
	 * Autowired Repositories
	 */
	@Autowired
	BusinessRepository businessRepository;
	@Autowired
	ProductsRepository productsRepository;

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
		initialiseDB();

		restTestClient = restTestClientAsBusiness(business1.getId());
	}

	Business business1;
	Business business2;
	Product product1;
	Product product2;

	void initialiseDB() {
		// TODO Could probably create a generic generation strategy using fixed size final variables,
		//  then re-using the collection of object(s) to compare against data stored in the Postgres DB.
		Business business1 = new Business("Business A", "123 Street", "FK8 2EE", "123@gmail.com", "01234567890");
		Business business2 = new Business("Business B", "456 Street", "FK8 2EE", "456@gmail.com", "07924823491");

		// save to theoretical postgres database
		this.business1 = businessRepository.save(business1);
		this.business2 = businessRepository.save(business2);

		Product product1 = new Product("milk", business1, "food", 10, 299L, "abc");
		Product product2 = new Product("bread", business2, "food", 15, 399L, "abc");

		this.product1 = productsRepository.save(product1);
		this.product2 = productsRepository.save(product2);


        productsRepository.save(product1);
        productsRepository.save(product2);
    }

	/**
	 * A test for adding products, where all mandatory fields are present.
	 */
	void addProductWithAllMandatoryDetails() {
		ProductCreationRequest productCreationRequest = new ProductCreationRequest();
		productCreationRequest.setName("milk");
		productCreationRequest.setCategory("food");
		productCreationRequest.setQuantity(10);
		productCreationRequest.setPrice(299L);

		String URI = String.format("/api/products/business/%s", businessRepository.findAll().getFirst().getId());

		var responseBody = restTestClient.post()
				.uri(URI)
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

		String URI = String.format("/api/products/business/%s", businessRepository.findAll().getFirst().getId());

		// Missing price post-request, expects 400 Bad Request
		restTestClient.post()
				.uri(URI)
				.body(productCreationRequest)
				.exchange()
				.expectStatus()
				.isBadRequest();
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

		String URI = String.format("/api/products/business/%s", NON_EXISTENT_BUSINESS_ID);

		restTestClient.post()
				.uri(URI)
				.body(productCreationRequest)
				.exchange()
				.expectStatus()
				.isNotFound();
	}

	/**
	 * A test for retrieving a product from a specific business from the api/products/business/{business_id} root.
	 *
	 * This checks for one product against a known list of products from this business which is of length 1.
	 */
	@Test
	void retrieveProductFromBusiness() {

		String URI = String.format("/api/products/business/%s", businessRepository.findAll().getFirst().getId().toString());

		var responseBody = restTestClient.get()
				.uri(URI)
				.exchange()
				.expectStatus().isOk()
				.expectHeader()
				.contentTypeCompatibleWith("application/hal+json")
				.expectBody()
				.jsonPath("$._embedded.productList.length()")
				.isEqualTo(1);


		assertNotNull(responseBody);
	}

	/**
	 * A test for retrieving all products from all businesses from the api/products/ root.
	 */
	@Test
	void retrieveAllProducts() {

		String URI = "/api/products";

		var responseBody = restTestClient.get()
				.uri(URI)
				.exchange()
				.expectStatus()
				.isOk()
				.expectHeader()
				.contentTypeCompatibleWith("application/hal+json")
				.expectBody()
				.jsonPath("$._embedded.productList.length()")
				.isEqualTo(2);

		assertNotNull(responseBody);
	}

	/**
	 * A test for retrieving a product by id from the api/products/{id} end-point.
	 */
	@Test
	void retrieveProductById() {
		Product product = productsRepository.findAll().getFirst();

		String URI = String.format("/api/products/%s", product.getId());
		var responseBody = restTestClient.get()
				.uri(URI)
				.exchange()
				.expectStatus().isOk()
				.expectHeader()
				.contentTypeCompatibleWith("application/hal+json")
				.expectBody()
				.jsonPath("$.id")
				.isEqualTo(product.getId().toString());

		assertNotNull(responseBody);
	}

	/**
	 * This test checks whether a non-authenticated user, using a malformed UUID can retrieve products.
	 *
	 * Expects: Fail - Bad Request 400.
	 */
	@Test
	void retrieveProductByIdWithMalformedUUID() {
		String URI = String.format("/api/products/%s", NON_EXISTENT_PRODUCT_ID);

		var responseBody = restTestClient.get()
				.uri(URI)
				.exchange()
				.expectStatus()
				.isBadRequest();
		assertNotNull(responseBody);
	}

	/**
	 * A test for updating a product with varying differing attributes.
	 */
	@Test
	 void updateProduct() {
		Product product = productsRepository.findAll().getFirst();

		ProductDetailUpdate detailUpdate = new ProductDetailUpdate();
		detailUpdate.setName("milk");
		detailUpdate.setCategory("food");
		detailUpdate.setQuantity(25);
		detailUpdate.setPrice(1400L);

		String URI = String.format("/api/products/%s", product.getId());

		restTestClient.put()
				.uri(URI)
				.body(detailUpdate)
				.exchange()
				.expectStatus()
				.isOk();
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

		String URI = String.format("/api/products/%s", NON_EXISTENT_PRODUCT_ID);

		restTestClient.put()
				.uri(URI)
				.body(detailUpdate)
				.exchange()
				.expectStatus()
				.isNotFound();
	}

	/**
	 * A test for updating a product with an invalid BUSINESS id.
	 */
	@Test
	void updateProductWithInvalidBusinessId() {
		Product product = productsRepository.findAll().getFirst();
		UUID productId = product.getId();

		ProductDetailUpdate detailUpdate = new ProductDetailUpdate();
		detailUpdate.setName("milk");
		detailUpdate.setCategory("food");
		detailUpdate.setQuantity(25);
		detailUpdate.setPrice(1400L);

		String URI = String.format("/api/products/%s", productId);

		restTestClient.put()
				.uri(URI)
				.body(detailUpdate)
				.exchange()
				.expectStatus()
				.isNotFound();
	}

	/**
	 * A test for updating a product with a negative price.
	 */
	@Test
	void updateProductWithInvalidPrice() {
		Product product = productsRepository.findAll().getFirst();
		UUID productId = product.getId();

		ProductDetailUpdate detailUpdate = new ProductDetailUpdate();
		detailUpdate.setPrice(-199L);
		detailUpdate.setQuantity(product.getQuantity());
		detailUpdate.setCategory(product.getCategory());
		detailUpdate.setName(product.getName());

		String URI = String.format("/api/products/%s", productId);

		restTestClient.put()
				.uri(URI)
				.body(detailUpdate)
				.exchange()
				.expectStatus()
				.isBadRequest();
	}

	/**
	 * Tests that a product can be deleted from a business.
	 *
	 * First check tests valid product ID and business ID, then checks that the product is deleted.
	 * Second check tests invalid product ID, then checks that the product is not deleted, returns NOT_FOUND: 404.
	 */
	@Test
	void deleteProduct() {

		Product product = productsRepository.findAll().getFirst();
		UUID productId = product.getId();

		String URI = String.format("/api/products/%s", productId);

		restTestClient.delete()
				.uri(URI)
				.exchange()
				.expectStatus()
				.isOk();
	}



	@Test
	void deleteProductWithInvalidProductId() {
		String URI = String.format("/api/products/%s", NON_EXISTENT_PRODUCT_ID);

		restTestClient.delete()
				.uri(URI)
				.exchange()
				.expectStatus()
				.isNotFound();
	}

	@Test
	void deleteProductWithInvalidBusinessId() {
		Product product = productsRepository.findAll().getFirst();
		UUID productId = product.getId();

		String URI = String.format("/api/products/business/%s/%s", NON_EXISTENT_BUSINESS_ID, productId);

		restTestClient.delete()
				.uri(URI)
				.exchange()
				.expectStatus()
				.isNotFound();
	}
}
