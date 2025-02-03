package backend.demo2;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MaishaSystemTest {
//for profile controller
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost/api/profile";
    }

    @Test
    public void testGetProfile_withValidUserId_returnsProfileDetails() {
        int validUserId = 1;

        given()
                .pathParam("userId", validUserId)
                .when()
                .get("/{userId}")
                .then()
                .statusCode(200)
                .body("user.id", equalTo(validUserId));
    }

    @Test
    public void testUpdateBio_withInvalidUserId_returnsNotFound() {
        int invalidUserId = 9999;

        given()
                .pathParam("userId", invalidUserId)
                .contentType("application/json")
                .body("{\"bio\":\"New bio content\"}")
                .when()
                .put("/{userId}/edit/bio")
                .then()
                .statusCode(404);
    }

    @Test
    public void testUpdateUsername_withValidUserId_updatesSuccessfully() {
        int validUserId = 1;

        Response response = given()
                .pathParam("userId", validUserId)
                .contentType("application/json")
                .body("{\"username\":\"maisha\"}")
                .when()
                .put("/{userId}/edit/username");

        response.then().statusCode(200);
        assertEquals("Username updated successfully.", response.getBody().asString());
    }

    @Test
    public void testDeleteAccount_withIncorrectPassword_returnsForbidden() {
        int validUserId = 1;

        Response response = given()
                .pathParam("userId", validUserId)
                .contentType("application/json")
                .body("{\"password\":\"wrongPassword\"}")
                .when()
                .delete("/{userId}/delete-account");

        response.then().statusCode(403);
        assertEquals("Incorrect password.", response.getBody().asString());
    }

    @Test
    public void testUpdateName_withValidUserId_updatesSuccessfully() {
        int validUserId = 1;

        Response response = given()
                .pathParam("userId", validUserId)
                .contentType("application/json")
                .body("{\"firstname\":\"NewFirstName\", \"lastname\":\"NewLastName\"}")
                .when()
                .put("/{userId}/edit/name");

        response.then().statusCode(200);
        assertEquals("Name updated successfully.", response.getBody().asString());
    }
    @Test
    public void testCreateProfile_withNonExistentUser_returnsNotFound() {
        int nonExistentUserId = 9999;

        Response response = given()
                .pathParam("userId", nonExistentUserId)
                .contentType("application/json")
                .body("{\"bio\":\"This is a bio\"}")
                .when()
                .post("/{userId}/createProfile");

        response.then().statusCode(404);
    }

    @Test
    public void testUpdateAvatar_withInvalidUserId_returnsNotFound() {
        int invalidUserId = 9999;

        Response response = given()
                .pathParam("userId", invalidUserId)
                .contentType("application/json")
                .body("{\"avatar\":\"newAvatarUrl\"}")
                .when()
                .put("/{userId}/edit/avatar");

        response.then().statusCode(404);
    }
    @Test
    public void testUpdatePassword_withCorrectCurrentPassword_updatesSuccessfully() {
        int validUserId = 1;

        Response response = given()
                .pathParam("userId", validUserId)
                .contentType("application/json")
                .body("{\"currentPassword\":\"hola\", \"newPassword\":\"hola\"}")
                .when()
                .put("/{userId}/update-password");

        response.then().statusCode(200);
        assertEquals("Password updated successfully.", response.getBody().asString());
    }

    @Test
    public void testGetProfile_withNonExistentUser_returnsNotFound() {
        int nonExistentUserId = 9999;

        Response response = given()
                .pathParam("userId", nonExistentUserId)
                .when()
                .get("/{userId}");

        response.then().statusCode(404);
    }
    @Test
    public void testUpdatePassword_withIncorrectCurrentPassword_returnsForbidden() {
        int validUserId = 21;

        Response response = given()
                .pathParam("userId", validUserId)
                .contentType("application/json")
                .body("{\"currentPassword\":\"wrongPassword\", \"newPassword\":\"newPassword123\"}")
                .when()
                .put("/{userId}/update-password");

        // Log the response body for inspection
        String responseBody = response.getBody().asString();
        System.out.println("Response Body: " + responseBody);

        // Assert status code
        response.then().statusCode(403);

        // Assert the plain text response
        assertEquals("Incorrect current password.", responseBody);
    }


}
