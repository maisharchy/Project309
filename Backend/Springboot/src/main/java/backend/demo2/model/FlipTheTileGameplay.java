package backend.demo2.model;


import backend.demo2.Server.Server;
import backend.demo2.User.User;
import jakarta.persistence.*;

@Entity
@Table(name = "flipthetile_gameplay")
public class FlipTheTileGameplay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private int score;

    private Long sessionId;

    @ManyToOne
    private Server server;

    public FlipTheTileGameplay() {}

    public FlipTheTileGameplay(User user, int score, Long sessionId, Server server) {
        this.user = user;
        this.score = score;
        this.sessionId = sessionId;
        this.server = server;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}

