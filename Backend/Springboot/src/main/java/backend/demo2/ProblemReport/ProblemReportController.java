package backend.demo2.ProblemReport;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/problem-reports")
public class ProblemReportController {

    @Autowired
    private ProblemReportService problemReportService;

    @Operation(summary = "Get all problem reports")
    @ApiResponse(responseCode = "200", description = "List of all problem reports retrieved successfully")
    @GetMapping
    public List<ProblemReport> getAllReports() {
        return problemReportService.getAllReports();
    }

    @Operation(summary = "Create a new problem report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<Long> createReport(@RequestBody ProblemReportRequest reportRequest) {
        ProblemReport createdReport = problemReportService.createReport(
                reportRequest.getUserId(), reportRequest.getTitle(), reportRequest.getDescription(), reportRequest.getType());

        Long createdReportId = createdReport.getId();
        return ResponseEntity.ok(createdReportId);
    }

    @Operation(summary = "Get a specific problem report by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem report found"),
            @ApiResponse(responseCode = "404", description = "Problem report not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProblemReport> getReportById(
            @Parameter(description = "ID of the problem report") @PathVariable Long id) {
        Optional<ProblemReport> report = problemReportService.getID(id);
        return report.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @Operation(summary = "Get all problem reports by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem reports retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No problem reports found for the user")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProblemReport>> getReportsByUserId(
            @Parameter(description = "User ID to filter reports") @PathVariable Long userId) {
        List<ProblemReport> reports = problemReportService.getReportsByUserId(userId);
        if (reports.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reports);
    }

    @Operation(summary = "Update an existing problem report by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem report updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Problem report not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProblemReport> updateReport(
            @Parameter(description = "ID of the problem report to be updated") @PathVariable Long id,
            @RequestBody ProblemReport updatedReport) {
        ProblemReport report = problemReportService.updateReport(id, updatedReport);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Delete a problem report by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Problem report deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Problem report not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        problemReportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}

