package com.example.androidexample;

public class GameListObject {
    private String game;
    private String isAssigned;

    public GameListObject(String game, String isAssigned) {
        this.game = game;
        this.isAssigned = isAssigned;
    }

    public GameListObject(String name) {
        this.game = name;
    }

    public String getGame() {
        return game;
    }

    public String getIsAssigned() {
        return isAssigned;
    }

    public void setIsAssigned(String isAssigned){
        this.isAssigned = isAssigned;
    }
}
