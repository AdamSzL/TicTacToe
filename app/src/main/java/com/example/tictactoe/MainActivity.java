package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button offlineModeButton;
    private Button onlineModeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        offlineModeButton = findViewById(R.id.offline_button);
        onlineModeButton = findViewById(R.id.online_button);

        offlineModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OfflineActivity.class);
                startActivity(intent);
            }
        });

        onlineModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OnlineActivity.class);
                startActivity(intent);
            }
        });
    }
}