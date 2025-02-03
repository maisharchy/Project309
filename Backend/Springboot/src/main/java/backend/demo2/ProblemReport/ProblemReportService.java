package backend.demo2.ProblemReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import backend.demo2.User.User;
import backend.demo2.User.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProblemReportService {

    @Autowired
    private ProblemReportRepository problemReportRepository;

    @Autowired
    private UserRepository userRepository;  // Autowire the UserRepository to check for user existence

    // Fetch all reports
    public List<ProblemReport> getAllReports() {
        return problemReportRepository.findAll();
    }

    // Create a new report
    public ProblemReport createReport(int userId, String title, String description, String type) {
        // Check if user exists
        User user = userRepository.findById((long) userId)
                .orElseThrow(() -> new IllegalArgumentException("User ID does not exist"));

        ProblemReport report = new ProblemReport(user, title, description, type, LocalDate.now());
        return problemReportRepository.save(report);
    }

    // Get a specific report by ID
    public Optional<ProblemReport> getID(Long id) {
        return problemReportRepository.findById(id);
    }

    // Update an existing report by ID
    public ProblemReport updateReport(Long id, ProblemReport updatedReport) {
        return problemReportRepository.findById(id)
                .map(report -> {
                    report.setTitle(updatedReport.getTitle());
                    report.setDescription(updatedReport.getDescription());
                    report.setType(updatedReport.getType());
                    return problemReportRepository.save(report);
                }).orElseThrow(() -> new IllegalArgumentException("Report with ID " + id + " not found."));
    }
    public List<ProblemReport> getReportsByUserId(Long userId) {
        return problemReportRepository.findByUserId(userId);
    }
    // Delete a report by ID
    public void deleteReport(Long id) {
        problemReportRepository.deleteById(id);
    }
}
