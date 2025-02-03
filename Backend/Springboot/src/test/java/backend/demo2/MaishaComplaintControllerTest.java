package backend.demo2;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import backend.demo2.Complaint.Complaint;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MaishaComplaintControllerTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost/api/complaints";
    }

    @Test
    public void testCreateComplaint_withValidData_createsSuccessfully() {
        Response response = given()
                .contentType("application/json")
                .body("{\"complainedUserId\":21,\"complainantUserId\":1,\"reason\":\"Spam\"}")
                .when()
                .post();

        response.then().statusCode(200);

        Long complaintId = response.jsonPath().getLong("complaintId");
        assertNotNull(complaintId, "Complaint ID should not be null");
    }


    @Test
    public void testUpdateComplaint_withValidId_returnsUpdatedComplaint() {
        // Prepare request body (ComplaintDTO)
        String complaintDTO = "{"
                + "\"complainedUserId\": 21,"
                + "\"complainantUserId\": 1,"
                + "\"reason\": \"Updated complaint reason\""
                + "}";

        // Send PUT request and verify response
        given()
                .contentType(ContentType.JSON)
                .body(complaintDTO)
                .when()
                .put("/16") // Assuming 16 is the valid complaint ID
                .then()
                .statusCode(200)
                .body("complaintId", equalTo(16))
                .body("complainantUser.id", equalTo(1)) // Correct path to access the complainantUser ID
                .body("complainedUser.id", equalTo(21))
                .body("reason", equalTo("Updated complaint reason"));
    }




    @Test
    public void testGetComplaintById_withInvalidId_returnsNotFound() {
        int invalidComplaintId = 9999;

        given()
                .pathParam("complaintId", invalidComplaintId)
                .when()
                .get("/{complaintId}")
                .then()
                .statusCode(404);
    }

    @Test
    public void testDeleteComplaint_withValidId_deletesSuccessfully() {
        int validComplaintId = 12;

        Response response = given()
                .pathParam("complaintId", validComplaintId)
                .when()
                .delete("/{complaintId}");

        response.then().statusCode(204); // Expect 204 No Content
    }

}
