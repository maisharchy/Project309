package backend.demo2.User;

import jakarta.persistence.*;

@Entity
public class SecurityQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question; // The question text

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user; // This will store the user who has selected the security question

    private String answer; // This will store the answer to the security question (hashed)

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}