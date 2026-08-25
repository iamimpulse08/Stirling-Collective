package co.uk.stirling_index.inventory;

import co.uk.stirling_index.inventory.model.business.Business;
import co.uk.stirling_index.inventory.model.security.Role;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:18");

    @DynamicPropertySource
    static void jwtProperties(DynamicPropertyRegistry registry) throws IOException {
        registry.add("jwt.public-key", JwtTestUtilities::getPublicKeyAsBase64);
        registry.add("jwt.private-key", JwtTestUtilities::getPrivateKeyAsBase64);
    }

    /**
     * Checks that the connection to the database is established.
     */
    @Test
    void connectionEstablished() {
        assertTrue(postgreSQLContainer.isCreated());
        assertTrue(postgreSQLContainer.isRunning());
    }
    /**
     * Rest Test Client
     */
    RestTestClient restTestClient;

    /**
     * This function sets the state of the RestTestClient to be authenticated as a random business, with a random business ID.
     */
    protected void setStateAsRandomBusiness() {
        restTestClient = restTestClientAsRandomBusiness();
    }

    /**
     * This function sets the state of the RestTestClient to be authenticated as a business, which is defined by the business parameter.
     * @param business - The business to authenticate as.
     * */
    protected void setStateAsBusiness(Business business) {
        restTestClient = restTestClientAsBusiness(business.getId());
    }

    protected RestTestClient setStateAsAnonymous() {
        restTestClient = restTestClientAnonymous();
        return restTestClient;
    }

    /**
     * Test Server Domain and Port Information
     */
    @LocalServerPort
    int port;
    String baseDomain = "http://localhost:";

    private String getBaseUrl() {
        return baseDomain + port;
    }

    /**
     * Rest Test Client Username / Emails
     */
    private final static String testEmail = "test-user@example.org";


    // fake IDs for consistent test case logic.
    final static String NON_EXISTENT_BUSINESS_ID = "NOT_A_REAL_UUID";
    final static String NON_EXISTENT_PRODUCT_ID = "NOT_A_REAL_UUID";

    protected RestTestClient restTestClientAs(Role role) {
        return RestTestClient.bindToServer()
                .baseUrl(getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION,
                        "Bearer " +
                                JwtTestUtilities.generateToken(testEmail, role, null)
                )
                .build();
    }

    protected RestTestClient restTestClientAsBusiness(UUID businessId) {
        return RestTestClient.bindToServer()
                .baseUrl(getBaseUrl())
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " +
                                JwtTestUtilities.generateToken(testEmail, Role.BUSINESS, businessId)
                )
                .build();
    }

    /**
     * Configures a RestTestClient to be authenticated as a random business - with a random business ID.
     * @return
     */
    protected RestTestClient restTestClientAsRandomBusiness() {
        return RestTestClient.bindToServer()
                .baseUrl(getBaseUrl())
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " +
                                JwtTestUtilities.generateToken(testEmail, Role.BUSINESS, UUID.randomUUID())
                        )
                .build();
    }

    protected RestTestClient restTestClientAnonymous() {
        return RestTestClient.bindToServer()
                .baseUrl(getBaseUrl())
                .build();
    }


}
