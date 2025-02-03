package backend.demo2;


import backend.demo2.User.User;
import backend.demo2.User.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MaishaLoginControllerTests {

    @LocalServerPort
    int port;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port; // Dynamically set the port
        RestAssured.baseURI = "http://localhost/api/login";


    }

    @Test
    void testLoginSuccess() {
        String requestBody = """
            {
                "username": "maisha",
                "password": "hola"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .body("message", equalTo("Login successful"))
                .body("userId", notNullValue());
    }

    @Test
    void testLoginInvalidPassword() {
        String requestBody = """
            {
                "username": "maisha",
                "password": "wrongpassword"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(401)
                .body("message", equalTo("Invalid username or password"))
                .body("userId", nullValue());
    }


}
