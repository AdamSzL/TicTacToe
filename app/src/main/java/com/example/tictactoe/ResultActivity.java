package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private TextView resultText;
    private Button menuButton;
    private Button newGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultText = findViewById(R.id.result_text);
        menuButton = findViewById(R.id.menu_button);
        newGameButton = findViewById(R.id.new_game_button);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            int result = extras.getInt("result");
            if (result == 0) {
                resultText.setText("Result: DRAW");
            } else if (result == 1) {
                resultText.setText("Result: PLAYER WON");
            } else if (result == 2) {
                resultText.setText("Result: COMPUTER WON");
            }
        }

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this, OfflineActivity.class);
                startActivity(intent);
            }
        });
    }
}