package com.example.tictactoe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

enum RoomStatus {
    WAITING,
    PLAYING,
    FINISHED,
    WAITING_FOR_REVENGE,
}

public class Room {
    RoomStatus status;
    List<String> playerIds;
    List<Integer> board;
    String currentPlayer;

    public Room() {
        this.status = RoomStatus.WAITING;
        this.playerIds = new ArrayList<String>();
        this.board = new ArrayList<Integer>();
        this.currentPlayer = "";
    }

    public Room(List<String> playerIds) {
        this.status = RoomStatus.WAITING;
        this.playerIds = playerIds;
        this.board = new ArrayList<Integer>(Collections.nCopies(9, 0));
        this.currentPlayer = playerIds.get(0);
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public List<String> getPlayerIds() {
        return playerIds;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public void setPlayerIds(List<String> playerIds) {
        this.playerIds = playerIds;
    }

    public void setBoard(List<Integer> board) {
        this.board = board;
    }
}
