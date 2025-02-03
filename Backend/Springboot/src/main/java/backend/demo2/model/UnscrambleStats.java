package backend.demo2.model;

import jakarta.persistence.*;

@Entity
@Table(name = "unscramble_stats")



public class UnscrambleStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private long gamesPlayed;
    private int highScore;
    private double averageScore;

    // No-argument constructor required by Hibernate
    public UnscrambleStats() {
    }


    public UnscrambleStats( String username, long gamesPlayed, int highScore, double averageScore) {

        this.username = username;
        this.gamesPlayed = gamesPlayed;
        this.highScore = highScore;
        this.averageScore = averageScore;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(long gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }
}



