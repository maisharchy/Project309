package backend.demo2.Trivia;

import backend.demo2.Server.Server;
import jakarta.persistence.*;

@Entity
@Table(name = "trivia_game_result")
public class TriviaGameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @Column(nullable = false)
    private Boolean multiplayer;  // "Yes" or "No"

    @Column(nullable = false)
    private Integer totalScore;  // Combined score for all users in the same server

    @Column(nullable = false)
    private Long sessionId;  // Unique session ID for each game

    @Column(nullable = false)
    private String result;  // Winner or Loser

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Boolean getMultiplayer() {
        return multiplayer;
    }

    public void setMultiplayer(Boolean multiplayer) {
        this.multiplayer = multiplayer;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
