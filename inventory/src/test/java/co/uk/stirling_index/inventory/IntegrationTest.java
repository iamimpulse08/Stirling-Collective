package co.uk.stirling_index.inventory;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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

    @LocalServerPort
    int port;

    // fake IDs for consistent test case logic.
    final static String NON_EXISTENT_BUSINESS_ID = "NOT_A_REAL_UUID";
    final static String NON_EXISTENT_PRODUCT_ID = "NOT_A_REAL_UUID";

    protected RestTestClient restTestClientAs(Role role) {
        return RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .defaultHeader(HttpHeaders.AUTHORIZATION,
                        "Bearer " +
                                JwtTestUtilities.generateToken("test-user@example.org", role, null)
                )
                .build();
    }

    protected RestTestClient restTestClientAsBusiness(UUID businessId) {
        return RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .defaultHeader(HttpHeaders.AUTHORIZATION,
                        "Bearer " +
                                JwtTestUtilities.generateToken("test-user@example.org", Role.BUSINESS, businessId)
                )
                .build();
    }

    protected RestTestClient restTestClientAnonymous() {
        return RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }


}
