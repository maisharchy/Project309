package backend.demo2;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

import backend.demo2.ProblemReport.ProblemReport;
import backend.demo2.ProblemReport.ProblemReportRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MaishaProblemReportControllerTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost/api/problem-reports";
    }


    @Test
    public void testGetProblemReportById_withValidId_returnsProblemReport() {
        int validReportId = 16;

        given()
                .pathParam("reportId", validReportId)
                .when()
                .get("/{reportId}")
                .then()
                .statusCode(200)
                .body("id", equalTo(validReportId));
    }

    @Test
    public void testGetProblemReportById_withInvalidId_returnsNotFound() {
        int invalidReportId = 9999;

        given()
                .pathParam("reportId", invalidReportId)
                .when()
                .get("/{reportId}")
                .then()
                .statusCode(404);
    }

    @Test
    public void testDeleteReport_Success() {
        Long id = 8L; // Replace with an existing report ID

        given()
                .pathParam("id", id) // Specify the path parameter
                .when()
                .delete("/{id}") // DELETE request to the endpoint
                .then()
                .statusCode(204)
                .body(equalTo(""));
    }
    @Test
    public void testGetAllProblemReports_returnsList() {
        given()
                .when()
                .get("")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0)); // Ensure the list is not empty
    }
    @Test
    public void testDeleteReport_withInvalidId_returnsNotFound() {
        Long invalidReportId = 9999L; // Replace with a non-existent report ID

        given()
                .pathParam("id", invalidReportId)
                .when()
                .delete("/api/problem-reports/{id}")
                .then()
                .statusCode(404); // Should return Not Found if the report doesn't exist
    }
    @Test
    public void testGetReportsByUserId_withNoReports_returnsNotFound() {
        Long invalidUserId = 9999L; // Replace with a non-existent user ID

        given()
                .pathParam("userId", invalidUserId)
                .when()
                .get("/api/problem-reports/user/{userId}")
                .then()
                .statusCode(404);
    }
    @Test
    public void testCreateReport_withValidData_returnsCreatedReportId() {
        // Create an instance of ProblemReportRequest using the default constructor
        ProblemReportRequest validRequest = new ProblemReportRequest();

        // Set the values using setters
        validRequest.setUserId(1);
        validRequest.setTitle("Test Title");
        validRequest.setDescription("Test Description");
        validRequest.setType("Test Type");

        // Perform the POST request
        given()
                .contentType("application/json")
                .body(validRequest)
                .when()
                .post("")
                .then()
                .statusCode(200) ;
    }
    @Test
    public void testUpdateReport_withValidData_returnsUpdatedReport() {
        Long reportId = 23L; // Existing report ID
        ProblemReport updatedReport = new ProblemReport();
        updatedReport.setTitle("Updated Title");
        updatedReport.setDescription("Updated Description");
        updatedReport.setType("Updated Type");

        given()
                .contentType("application/json")
                .body(updatedReport)
                .pathParam("id", reportId)
                .when()
                .put("{id}")
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Title"))
                .body("description", equalTo("Updated Description"))
                .body("type", equalTo("Updated Type"));
    }



}
