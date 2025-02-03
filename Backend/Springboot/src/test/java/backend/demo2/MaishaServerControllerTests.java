package backend.demo2;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MaishaServerControllerTests {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port; // Dynamically set the port
        RestAssured.baseURI = "http://localhost/api/servers";
    }

    @Test
    void testCreateServer() {
        String requestBody = """
            {
                "name": "Test Server",
                "userId": 1
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/create")
                .then()
                .statusCode(200)
                .body("name", equalTo("Test Server"))
                .body("memberships", not(empty()));
    }

    @Test
    void testJoinServer() {
        String requestBody = """
            {
                "serverId": 14,
                "userId": 1
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/join")
                .then()
                .statusCode(200)
                .body(equalTo("User successfully joined the server as a player"));
    }

    @Test
    void testEnterServer() {
        int serverId = 10;
        long userId = 1;

        given()
                .when()
                .post("/enter/{serverId}/{userId}", serverId, userId)
                .then()
                .statusCode(200)
                .body(containsString("User entered server:"))
                .body(containsString("User Type:"));
    }

    @Test
    void testLeaveServer() {
        int serverId = 14;
        long userId = 1;

        given()
                .when()
                .delete("/leave/{serverId}/{userId}", serverId, userId)
                .then()
                .statusCode(200)
                .body(equalTo("User has left the server"));
    }
    @Test
    void testCreateServerWithInvalidUser() {
        String requestBody = """
        {
            "name": "Test Server",
            "userId": 99999
        }
    """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body(isEmptyOrNullString());
    }
    @Test
    void testJoinServerWhenAlreadyAMember() {
        String requestBody = """
        {
            "serverId": 14,
            "userId": 1
        }
    """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/join")
                .then()
                .statusCode(400)
                .body(equalTo("User already a member of the server"));
    }

    @Test
    void testLeaveServerWhenNotAMember() {
        int serverId = 14;
        long userId = 99999; // User who is not a member

        given()
                .when()
                .delete("/leave/{serverId}/{userId}", serverId, userId)
                .then()
                .statusCode(400)
                .body(equalTo("Server or User not found"));
    }
    
    @Test
    void testGetProblemReportsForServer() {
        int serverId = 3;

        given()
                .when()
                .get("/{serverId}/problem-reports", serverId)
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))  // Ensure problem reports are returned
                .log().all();  // Log the response body for debugging
    }

}
