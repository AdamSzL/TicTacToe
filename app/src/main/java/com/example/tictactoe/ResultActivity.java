package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView resultText = findViewById(R.id.result_text);
        Button menuButton = findViewById(R.id.menu_button);
        Button newGameButton = findViewById(R.id.new_game_button);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            int result = extras.getInt("result");
            if (result == 0) {
                resultText.setText(getResources().getString(R.string.result_draw));
            } else if (result == 1) {
                resultText.setText(getResources().getString(R.string.result_player_won));
            } else if (result == 2) {
                resultText.setText(getResources().getString(R.string.result_computer_won));
            }
        }

        menuButton.setOnClickListener(view -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);
        });

        newGameButton.setOnClickListener(view -> {
            Intent intent = new Intent(ResultActivity.this, OfflineActivity.class);
            startActivity(intent);
        });
    }
}