package backend.demo2.ProblemReport;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;
import backend.demo2.User.User;

import java.time.LocalDate;

@Entity
@Table(name = "problem_reports")
public class ProblemReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)  // Only persist changes to user
    @JoinColumn(name = "user_id")  // Foreign key reference to User's id
    private User user;


    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private String type;

    @NonNull
    private LocalDate date;

    // Default constructor
    public ProblemReport() {}

    public ProblemReport(User user, String title, String description, String type, LocalDate date) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.type = type;
        this.date = date;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

