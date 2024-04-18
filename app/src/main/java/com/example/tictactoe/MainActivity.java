package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button offlineModeButton = findViewById(R.id.offline_button);
        Button onlineModeButton = findViewById(R.id.online_button);

        offlineModeButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, OfflineActivity.class);
            startActivity(intent);
        });

        onlineModeButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, OnlineActivity.class);
            startActivity(intent);
        });
    }
}