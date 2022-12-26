package com.example.tictactoe;

import android.util.Log;

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
    List<String> playerIds = new ArrayList<String>();
    List<Integer> board = new ArrayList<Integer>();
    String currentPlayer;

    public Room() {
        this.status = RoomStatus.WAITING;
        this.playerIds = new ArrayList<String>();
        this.board = new ArrayList<Integer>();
        this.currentPlayer = "";
    }

    public Room(RoomStatus status, List<String> playerIds, List<Integer> board, String currentPlayer) {
        this.status = status;
        this.playerIds = playerIds;
        this.board = board;
        this.currentPlayer = currentPlayer;
    }

    public Room(List<String> playerIds) {
        this.status = RoomStatus.WAITING;
        Log.d("xxx", String.valueOf(playerIds.size()));
        this.playerIds = playerIds;
        this.board = new ArrayList<Integer>(Collections.nCopies(9, 0));
        this.currentPlayer = playerIds.get(0);
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public List<String> getPlayerIds() {
        return playerIds;
    }

    public List<Integer> getBoard() {
        return board;
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
