package com.example.tictactoe;

import java.util.List;

public class Helpers {
    public static boolean checkIfBoardFull(List<Integer> board) {
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i) == 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkIfWon(List<Integer> board, int player) {
        for (int i = 0; i < 9; i += 3) {
            if (board.get(i) == player && board.get(i+1) == player && board.get(i+2) == player) {
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (board.get(i) == player && board.get(i + 3) == player && board.get(i + 6) == player) {
                return true;
            }
        }

        if (board.get(2) == player && board.get(4) == player && board.get(6) == player) {
            return true;
        }

        if (board.get(0) == player && board.get(4) == player && board.get(8) == player) {
            return true;
        }

        return false;
    }
}
