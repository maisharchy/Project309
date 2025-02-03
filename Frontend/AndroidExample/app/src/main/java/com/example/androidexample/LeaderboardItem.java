package com.example.androidexample;

public class LeaderboardItem {
    private String username;
    private int highScore;
    private long gamesPlayed;
    private double averageScore;

    public LeaderboardItem(String username, int highScore, long gamesPlayed, double averageScore) {
        this.username = username;
        this.highScore = highScore;
        this.gamesPlayed = gamesPlayed;
        this.averageScore = averageScore;
    }

    // Getters
    public String getUsername() { return username; }
    public int getHighScore() { return highScore; }
    public long getGamesPlayed() { return gamesPlayed; }
    public double getAverageScore() { return averageScore; }
}