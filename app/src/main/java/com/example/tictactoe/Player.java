package com.example.tictactoe;

public class Player {
    String name;

    public Player() {
        this.name = "Player";
    }

    Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
