package backend.demo2.Complaint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import backend.demo2.User.User;
import backend.demo2.User.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository; // Add this line to inject UserRepository

    // Get all complaints
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    // Create a new complaint
    public Complaint createComplaint(Long complainedUserId, Long complainantUserId, String reason) {
        User complainedUser = userRepository.findById(complainedUserId)
                .orElseThrow(() -> new RuntimeException("Complained user not found"));
        User complainantUser = userRepository.findById(complainantUserId)
                .orElseThrow(() -> new RuntimeException("Complainant user not found"));

        Complaint complaint = new Complaint(complainedUser, complainantUser, reason, LocalDate.now());
        return complaintRepository.save(complaint);
    }


    // Get a complaint by ID
    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }

    // Delete a complaint by ID
    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }

    // Update a complaint
    public Complaint updateComplaint(Long id, Long complainedUserId, Long complainantUserId, String reason) {
        Optional<Complaint> optionalComplaint = complaintRepository.findById(id);
        if (optionalComplaint.isPresent()) {
            Complaint complaint = optionalComplaint.get();

            // Update the complained and complainant users
            User complainedUser = userRepository.findById(complainedUserId)
                    .orElseThrow(() -> new RuntimeException("Complained user not found")); // Handle user not found
            User complainantUser = userRepository.findById(complainantUserId)
                    .orElseThrow(() -> new RuntimeException("Complainant user not found")); // Handle user not found

            complaint.setComplainedUser(complainedUser);
            complaint.setComplainantUser(complainantUser);
            complaint.setReason(reason);
            return complaintRepository.save(complaint);
        } else {
            throw new RuntimeException("Complaint not found"); // Handle not found case
        }
    }
    // Method to fetch complaints by complainant user ID
    public List<Complaint> getComplaintsByComplainantUserId(int complainantUserId) {
        return complaintRepository.findByComplainantUserId(complainantUserId);
    }
}
