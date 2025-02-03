package backend.demo2.Complaint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Operation(summary = "Get all complaints")
    @ApiResponse(responseCode = "200", description = "List of all complaints retrieved successfully")
    @GetMapping
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @Operation(summary = "Create a new complaint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Complaint created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<Map<String, Long>> createComplaint(@RequestBody ComplaintDTO complaintDTO) {
        // Create the complaint
        Complaint createdComplaint = complaintService.createComplaint(
                complaintDTO.getComplainedUserId(),
                complaintDTO.getComplainantUserId(),
                complaintDTO.getReason());

        // Prepare a map with the complaintId to return as a response
        Map<String, Long> response = new HashMap<>();
        response.put("complaintId", createdComplaint.getComplaintId());

        // Return only the complaintId in the response
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a specific complaint by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Complaint found"),
            @ApiResponse(responseCode = "404", description = "Complaint not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaintById(
            @Parameter(description = "ID of the complaint to be retrieved") @PathVariable Long id) {
        Optional<Complaint> complaint = complaintService.getComplaintById(id);
        return complaint.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a complaint by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Complaint deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Complaint not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update an existing complaint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Complaint updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Complaint not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Complaint> updateComplaint(
            @Parameter(description = "ID of the complaint to be updated") @PathVariable Long id,
            @RequestBody ComplaintDTO complaintDTO) {
        Complaint updatedComplaint = complaintService.updateComplaint(
                id,
                complaintDTO.getComplainedUserId(),
                complaintDTO.getComplainantUserId(),
                complaintDTO.getReason());
        return ResponseEntity.ok(updatedComplaint);
    }

    @Operation(summary = "Get complaints by complainant user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Complaints found for the complainant user"),
            @ApiResponse(responseCode = "404", description = "No complaints found for the given complainant user ID")
    })
    @GetMapping("/complainant/{complainantUserId}")
    public ResponseEntity<List<Complaint>> getComplaintsByComplainantUserId(
            @Parameter(description = "Complainant user ID") @PathVariable int complainantUserId) {
        List<Complaint> complaints = complaintService.getComplaintsByComplainantUserId(complainantUserId);
        if (complaints.isEmpty()) {
            return ResponseEntity.notFound().build(); // Return 404 if no complaints found
        }
        return ResponseEntity.ok(complaints); // Return the list of complaints
    }
}
