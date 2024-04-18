package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class OfflineActivity extends AppCompatActivity {

    private TextView player1Text;
    private TextView player2Text;

    private final ArrayList<ImageView> tiles = new ArrayList<ImageView>();

    private final ArrayList<Integer> board = new ArrayList<Integer>(Collections.nCopies(9, 0));

    int currentPlayer = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        player1Text = findViewById(R.id.player_1_text);
        player2Text = findViewById(R.id.player_2_text);

        for (int i = 0; i < 9; i++) {
            final int j = i;
            int res = getResources().getIdentifier("tile_" + (i + 1), "id", getPackageName());
            ImageView tile = (ImageView) findViewById(res);
            tile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentPlayer == 1) {
                        makeMove(tile, 1, j);

                        if (!Helpers.checkIfWon(board, 1) && !Helpers.checkIfWon(board, 2) && !Helpers.checkIfBoardFull(board)) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    makeRandomMove();

                                    checkGameEnd();
                                }
                            }, 500);
                        }

                        checkGameEnd();
                    }
                }
            });
            tiles.add(tile);
        }
    }

    private void checkGameEnd() {
        if (Helpers.checkIfWon(board, 1)) {
            Intent intent = new Intent(OfflineActivity.this, ResultActivity.class);
            intent.putExtra("result", 1);
            startActivity(intent);
        } else if (Helpers.checkIfWon(board, 2)) {
            Intent intent = new Intent(OfflineActivity.this, ResultActivity.class);
            intent.putExtra("result", 2);
            startActivity(intent);
        } else if (Helpers.checkIfBoardFull(board)) {
            Intent intent = new Intent(OfflineActivity.this, ResultActivity.class);
            intent.putExtra("result", 0);
            startActivity(intent);
        }
    }

    private void makeMove(ImageView tile, int player, int index) {
        if (!Helpers.checkIfWon(board, 1) && !Helpers.checkIfWon(board, 2) && !Helpers.checkIfBoardFull(board) && board.get(index) == 0) {
            board.set(index, player);
            player2Text.setTextColor(ContextCompat.getColor(OfflineActivity.this, (player == 1 ? R.color.white : R.color.dark_grey)));
            player1Text.setTextColor(ContextCompat.getColor(OfflineActivity.this, (player == 1 ? R.color.dark_grey : R.color.white)));
            tile.setBackgroundResource((player == 1 ? R.drawable.cross : R.drawable.circle));
            currentPlayer = (player == 1 ? 2 : 1);
        }
    }

    private void makeRandomMove() {
        if (!Helpers.checkIfBoardFull(board)) {
            int n = getRandomField();
            while(board.get(n) != 0) {
                n = getRandomField();
            }
            int res = getResources().getIdentifier("tile_" + (n + 1), "id", getPackageName());
            ImageView tile = (ImageView) findViewById(res);
            makeMove(tile, 2, n);
        }
    }

    private int getRandomField() {
        Random rand = new Random();
        return rand.nextInt(9);
    }

    private static String printBoard(ArrayList<Integer> board) {
        String text = "";
        for (int i = 0; i < board.size(); i++) {
            text += String.valueOf(board.get(i)) + " ";
        }
        return text;
    }
}