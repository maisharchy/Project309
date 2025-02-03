package backend.demo2.GuessTheNumber;

import backend.demo2.User.User; // Import the User entity
import jakarta.persistence.*;

@Entity
public class PlayerScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key to User
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_session_id", nullable = false) // Foreign key to GameSession
    private GameSession gameSession;

    @Column(nullable = false)
    private int score;

    // Getters and Setters
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

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    // Adjusted method to get the userId using the User's getId method
    public Integer getUserId() {
        return user != null ? user.getId() : null;  // Access the user's id via getId() method
    }
}
