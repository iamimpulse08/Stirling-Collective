package co.uk.stirling_index.inventory;

import co.uk.stirling_index.inventory.model.business.Business;
import co.uk.stirling_index.inventory.model.product.dto.ProductCreationRequest;
import co.uk.stirling_index.inventory.model.product.dto.ProductDetailUpdate;
import co.uk.stirling_index.inventory.model.product.Product;
import co.uk.stirling_index.inventory.service.ProductService;
import co.uk.stirling_index.inventory.service.repository.BusinessRepository;
import co.uk.stirling_index.inventory.service.repository.ProductsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InventoryProductControllerTest extends IntegrationTest {
	/**
	 * Autowired Repositories
	 */
	@Autowired
	BusinessRepository businessRepository;
	@Autowired
	ProductsRepository productsRepository;
	@Autowired
	ProductService productService;

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

		restTestClient = restTestClientAnonymous();
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

	private Product getFirstProductOfBusiness(Business business) {
		return productService.getAllProducts(business.getId()).getFirst();
	}

	/**
	 * A test for adding products, where all mandatory fields are present.
	 */
	@Test
	void addProductWithAllMandatoryDetailsExpectOk() {
		setStateAsBusiness(business1);

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
	void addProductWithMissingMandatoryDetailsExpectBadRequest() {
		setStateAsBusiness(business1);

		ProductCreationRequest productCreationRequest = new ProductCreationRequest();
		productCreationRequest.setName("milk");
		productCreationRequest.setCategory("food");
		productCreationRequest.setQuantity(10);

		String URI = String.format("/api/products/business/%s", business1);

		// Missing price post-request, expects 400 Bad Request
		restTestClient.post()
				.uri(URI)
				.body(productCreationRequest)
				.exchange()
				.expectStatus()
				.isBadRequest();
	}

	@Test
	void addProductOfAnotherBusinessExpectForbidden() {
		setStateAsBusiness(business1);
		ProductCreationRequest productCreationRequest = new ProductCreationRequest();
		productCreationRequest.setName("milk");
		productCreationRequest.setCategory("food");
		productCreationRequest.setQuantity(10);
		productCreationRequest.setPrice(299L);

		String URI = String.format("/api/products/business/%s", business2.getId());

		restTestClient.post()
				.uri(URI)
				.body(productCreationRequest)
				.exchange()
				.expectStatus()
				.isForbidden();
	}


	/**
	 * A test for adding products, where the business id is invalid.
	 */
	@Test
	void addProductWithInvalidBusinessIdExpectBadRequest() {
		setStateAsBusiness(business1);

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
				.isBadRequest();
	}

	/**
	 * A test for retrieving a product from a specific business from the api/products/business/{business_id} root.
	 *
	 * This checks for one product against a known list of products from this business which is of length 1.
	 */
	@Test
	void retrieveProductFromBusinessExpectOk() {
		UUID businessId = business1.getId();
		String URI = String.format("/api/products/business/%s", businessId);

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
	void retrieveAllProductsExpectOk() {

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
	void retrieveProductByIdExpectOk() {
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
	void retrieveProductByIdWithMalformedUUIDExpectBadRequest() {
		String URI = String.format("/api/products/%s", NON_EXISTENT_PRODUCT_ID);

		var responseBody = restTestClient.get()
				.uri(URI)
				.exchange()
				.expectStatus()
				.isBadRequest();
		assertNotNull(responseBody);
	}

	/**
	 * This test initially sets the state of the test mocking a request bearing the authorisation of Business 1.
	 * This test ensures that business 1 can correctly update attributes of a product of business 1.
	 * <p>
	 * This test expects status code 200: OK.
	 */
	@Test
	 void updateProductExpectOk() {
		setStateAsBusiness(business1);
		Product product = getFirstProductOfBusiness(business1);

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
	void updateProductWithInvalidIdExpectBadRequest() {
		setStateAsBusiness(business1);

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
				.isBadRequest();
	}

	@Test
	void updateProductOfAnotherBusinessExpectForbidden() {
		setStateAsBusiness(business1);
		Product product = getFirstProductOfBusiness(business2);
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
				.isForbidden();
	}

	/**
	 * A test for updating a product with an invalid BUSINESS id.
	 */
	@Test
	void updateProductAsNonExistentBusinessExpectForbidden() {

		// TODO this doesnt work properly because it expects fail, but the business that this product belongs to, the RestTestClient currently acts as.
		Product product = productsRepository.findAll().getFirst();
		UUID productId = product.getId();

		ProductDetailUpdate detailUpdate = new ProductDetailUpdate();
		detailUpdate.setName("milk");
		detailUpdate.setCategory("food");
		detailUpdate.setQuantity(25);
		detailUpdate.setPrice(1400L);

		String URI = String.format("/api/products/%s", productId);

		restTestClient = restTestClientAsRandomBusiness();

		restTestClient.put()
				.uri(URI)
				.body(detailUpdate)
				.exchange()
				.expectStatus()
				.isForbidden();
	}

	/**
	 * A test for updating a product with a negative price.
	 */
	@Test
	void updateProductWithInvalidPriceExpectBadRequest() {
		setStateAsBusiness(business1);
		Product product = getFirstProductOfBusiness(business1);
		UUID productId = product.getId();

		// set product update requests.
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
	void deleteProductExpectOk() {
		setStateAsBusiness(business1);
		Product product = getFirstProductOfBusiness(business1);
		UUID productId = product.getId();

		String URI = String.format("/api/products/%s", productId);

		restTestClient.delete()
				.uri(URI)
				.exchange()
				.expectStatus()
				.isOk();
	}

	@Test
	void deleteProductWithInvalidProductIdExpectForbidden() {
		String URI = String.format("/api/products/%s", NON_EXISTENT_PRODUCT_ID);

		restTestClient.delete()
				.uri(URI)
				.exchange()
				.expectStatus()
				.isForbidden();
	}

	/**
	 * This method tests that a product cannot be deleted from a business that is not the owner of the product.
	 * <p>
	 * This method sets the state of the test mocking a request bearing the authorisation of Business 1.
	 * <p>
	 * This test expects status code 403: Forbidden.
	 */
	@Test
	void deleteProductOfAnotherBusinessExpectForbidden() {
		setStateAsBusiness(business1);
		Product product = getFirstProductOfBusiness(business2);
		UUID productId = product.getId();

		String URI = String.format("/api/products/%s", productId);

		restTestClient.delete()
				.uri(URI)
				.exchange()
				.expectStatus()
				.isForbidden();
	}
}
