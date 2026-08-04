package co.uk.stirling_index.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class AuthenticationControllerTest extends IntegrationTest {


    @Test
    void shouldSuccessfullyLogin() {
        restTestClient.get().uri("/api/v1/auth/login")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldPersistRegisteredUser() {



        restTestClient.post().uri("/api/v1/auth/register")
                .exchange()
                .expectStatus().isCreated();
    }



}

