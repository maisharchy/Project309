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
class MaishaPasswordResetControllerTests {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port; // Set the dynamic port
        RestAssured.baseURI = "http://localhost/api/password";
    }

    @Test
    void testResetPasswordSuccess() {
        String requestBody = """
            {
                "email": "maisha.rchy@gmail.com",
                "password": "hola",
                "securityQuestionAnswer": "Takasugi"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/reset")
                .then()
                .statusCode(200)
                .body(equalTo("Password reset was successful! Log in to view content"));
    }

    @Test
    void testResetPasswordIncorrectAnswer() {
        String requestBody = """
            {
                "email": "maisha.rchy@gmail.com",
                "password": "newPassword123",
                "securityQuestionAnswer": "WrongAnswer"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/reset")
                .then()
                .statusCode(403)
                .body(equalTo("Incorrect answer to the security question."));
    }

    @Test
    void testResetPasswordUserNotFound() {
        String requestBody = """
            {
                "email": "unknown.email@gmail.com",
                "password": "newPassword123",
                "securityQuestionAnswer": "Gintama"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/reset")
                .then()
                .statusCode(404)
                .body(equalTo("User with email unknown.email@gmail.com not found."));
    }

    @Test
    void testGetSecurityQuestionByUserIdSuccess() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/securityQuestion/1")
                .then()
                .statusCode(200)
                .body(equalTo("Who is my favorite character in Gintama?"));
    }

    @Test
    void testGetSecurityQuestionUserNotFound() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/securityQuestion/999")
                .then()
                .statusCode(404)
                .body(equalTo("User with ID 999 not found."));
    }

    @Test
    void testSetOrUpdateSecurityQuestionSuccess() {
        String requestBody = """
            {
                "email": "maisha.rchy@gmail.com",
                "question": "Who is my favorite character in Gintama?",
                "answer": "Takasugi"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/setSecurityQuestion")
                .then()
                .statusCode(200)
                .body(equalTo("Security question updated successfully."));
    }

    @Test
    void testSetOrUpdateSecurityQuestionUserNotFound() {
        String requestBody = """
            {
                "email": "unknown.email@gmail.com",
                "question": "What is my favorite movie?",
                "answer": "Inception"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/setSecurityQuestion")
                .then()
                .statusCode(404)
                .body(equalTo("User with email unknown.email@gmail.com not found."));
    }
    @Test
    void testResetPasswordEmptyEmail() {
        String requestBody = """
            {
                "email": "",
                "password": "newPassword123",
                "securityQuestionAnswer": "Takasugi"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/reset")
                .then()
                .statusCode(404)  // Assuming the system will return a bad request for empty email
                .body(equalTo("User with email  not found."));
    }

    // New Test: Check for empty password field
    @Test
    void testResetPasswordEmptyPassword() {
        String requestBody = """
            {
                "email": "maisha.rchy@gmail.com",
                "password": "",
                "securityQuestionAnswer": "Takasugi"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/reset")
                .then()
                .statusCode(400)  // Assuming the system will return a bad request for empty password
                .body(equalTo("Password field is required."));
    }

    // New Test: Check for invalid email format
    @Test
    void testResetPasswordInvalidEmailFormat() {
        String requestBody = """
            {
                "email": "invalidemail.com",
                "password": "newPassword123",
                "securityQuestionAnswer": "Takasugi"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/reset")
                .then()
                .statusCode(404)
                .body(equalTo("User with email invalidemail.com not found."));
    }

    // New Test: Check for missing security question answer
    @Test
    void testResetPasswordMissingSecurityQuestionAnswer() {
        String requestBody = """
            {
                "email": "maisha.rchy@gmail.com",
                "password": "newPassword123",
                "securityQuestionAnswer": ""
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/reset")
                .then()
                .statusCode(403)
                .body(equalTo("Incorrect answer to the security question."));
    }

    // New Test: Check for non-matching email and security question answer
    @Test
    void testResetPasswordNonMatchingEmailAnswer() {
        String requestBody = """
            {
                "email": "maisha.rchy@gmail.com",
                "password": "newPassword123",
                "securityQuestionAnswer": "IncorrectAnswer"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/reset")
                .then()
                .statusCode(403)
                .body(equalTo("Incorrect answer to the security question."));
    }
}
