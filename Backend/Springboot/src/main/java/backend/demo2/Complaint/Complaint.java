package backend.demo2.Complaint;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;
import backend.demo2.User.User;

import java.time.LocalDate;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintId;

    // Foreign key to the User's table (user who is being complained against)
    @ManyToOne // Define relationship
    @JoinColumn(name = "complained_user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User complainedUser;

    // Foreign key to the User's table (user who made the complaint)
    @ManyToOne // Define relationship
    @JoinColumn(name = "complainant_user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User complainantUser;

    @NonNull
    private String reason;

    @NonNull
    private LocalDate dateMade;

    // Default constructor
    public Complaint() {}

    public Complaint(User complainedUser, User complainantUser, String reason, LocalDate dateMade) {
        this.complainedUser = complainedUser;
        this.complainantUser = complainantUser;
        this.reason = reason;
        this.dateMade = dateMade;
    }

    // Getters and setters
    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public User getComplainedUser() {
        return complainedUser;
    }

    public void setComplainedUser(User complainedUser) {
        this.complainedUser = complainedUser;
    }

    public User getComplainantUser() {
        return complainantUser;
    }

    public void setComplainantUser(User complainantUser) {
        this.complainantUser = complainantUser;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getDateMade() {
        return dateMade;
    }

    public void setDateMade(LocalDate dateMade) {
        this.dateMade = dateMade;
    }
}

