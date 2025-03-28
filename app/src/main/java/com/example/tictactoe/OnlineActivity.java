package com.example.tictactoe;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineActivity extends AppCompatActivity {

    private LinearLayout waitingContainer;

    private final ArrayList<ImageView> tiles = new ArrayList<>();

    private TextView player1Text;
    private TextView player2Text;

    private DatabaseReference roomsRef;
    private DatabaseReference roomRef;
    private DatabaseReference playersRef;
    private final HashMap<String, Room> roomsMap = new HashMap<>();
    private String playerKey;
    private String enemyKey;
    private String nickname;
    private String roomKey;
    private int currentPlayer;
    private long connectionTime;
    private long currentTime;
    private final long timeout = 10000;
    private boolean shouldRunAgain = true;
    private boolean waitingAlertShown = false;
    private AlertDialog waitingAlert;

    private Room gameRoom;

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        waitingContainer = findViewById(R.id.waiting_container);

        for (int i = 0; i < 9; i++) {
            final int j = i;
            int res = getResources().getIdentifier("tile_" + (i + 1), "id", getPackageName());
            ImageView tile = findViewById(res);
            tile.setOnClickListener(view -> cellClicked(tile, j));
            tiles.add(tile);
        }

        player1Text = findViewById(R.id.player_1_text);
        player2Text = findViewById(R.id.player_2_text);

        playersRef = db.getReference("players");

        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineActivity.this);
        builder.setTitle(getResources().getString(R.string.online_game));
        builder.setMessage(getResources().getString(R.string.enter_nickname));

        EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton(getResources().getString(R.string.play), (dialogInterface, i) -> {
            nickname = input.getText().toString();
            if (nickname.isEmpty()) {
                finish();
            }
            else {
                Date date = new Date();
                connectionTime = date.getTime();
                currentTime = connectionTime;
                playerKey = playersRef.push().getKey();
                playersRef.child(playerKey).setValue(new Player(nickname));
                findRoom();
                startInterval();
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialogInterface, i) -> finish());

        builder.show();
    }

    private void findRoom() {
        roomsRef = db.getReference("rooms");

        roomsRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                DataSnapshot dataSnapshot = task.getResult();
                boolean shouldInsertNew = true;

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    roomKey = snapshot.getKey();
                    Room room = snapshot.getValue(Room.class);
                    roomsMap.put(roomKey, room);
                    List<String> playerIds = room.getPlayerIds();
                    if (playerIds.size() == 1) {
                        roomRef = snapshot.getRef();
                        playerIds.add(playerKey);
                        room.setPlayerIds(playerIds);
                        Map<String, Object> roomMap = new HashMap<String, Object>();
                        roomMap.put(roomKey, room);
                        roomsRef.updateChildren(roomMap);
                        handleRoomUpdates();
                        shouldInsertNew = false;
                        currentPlayer = 2;
                    }
                }

                if (shouldInsertNew || roomsMap.isEmpty()) {
                    insertNewRoom();
                }
            }
        });
    }

    private void insertNewRoom() {
        List<String> playerIds = new ArrayList<String>(Arrays.asList(playerKey));
        Room room = new Room(playerIds);
        roomKey = roomsRef.push().getKey();
        roomsRef.child(roomKey).setValue(room);
        roomRef = roomsRef.child(roomKey);
        currentPlayer = 1;
        handleRoomUpdates();
    }

    private void handleRoomUpdates() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String roomKey = snapshot.getKey();
                if (snapshot.getValue() == null) return;
                Room room = snapshot.getValue(Room.class);
                gameRoom = room;
                if (room.playerIds.size() == 2 && room.status == RoomStatus.WAITING) {
                    room.setStatus(RoomStatus.PLAYING);
                    shouldRunAgain = false;
                    if (waitingAlert != null) {
                        waitingAlert.dismiss();
                    }
                    Map<String, Object> roomMap = new HashMap<String, Object>();
                    roomMap.put(roomKey, room);
                    roomsRef.updateChildren(roomMap);
                    waitingContainer.setVisibility(View.GONE);

                    List<String> ids = new ArrayList<>(room.playerIds);
                    ids.removeIf(id -> id.equals(playerKey));
                    enemyKey = ids.get(0);
                    playersRef.child(enemyKey).get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            DataSnapshot dataSnapshot = task.getResult();
                            Player player = dataSnapshot.getValue(Player.class);
                            if (currentPlayer == 1) {
                                player1Text.setText(nickname);
                                player2Text.setText(player.getName());
                            } else {
                                player2Text.setText(nickname);
                                player1Text.setText(player.getName());
                            }
                        }
                    });

                    fixStartingPlayer();
                } else if (room.status == RoomStatus.PLAYING) {
                    if (room.currentPlayer.equals(playerKey)) {
                        player1Text.setTextColor(ContextCompat.getColor(OnlineActivity.this, (currentPlayer == 1 ? R.color.white : R.color.dark_grey)));
                        player2Text.setTextColor(ContextCompat.getColor(OnlineActivity.this, (currentPlayer == 1 ? R.color.dark_grey : R.color.white)));
                    } else {
                        player1Text.setTextColor(ContextCompat.getColor(OnlineActivity.this, (currentPlayer == 1 ? R.color.dark_grey : R.color.white)));
                        player2Text.setTextColor(ContextCompat.getColor(OnlineActivity.this, (currentPlayer == 1 ? R.color.white : R.color.dark_grey)));
                    }
                    updateTiles();
                    String result = "";
                    if (Helpers.checkIfWon(gameRoom.board, currentPlayer)) {
                        result = getResources().getString(R.string.you_won);
                    } else if (Helpers.checkIfWon(gameRoom.board, currentPlayer == 1 ? 2 : 1)) {
                        result = getResources().getString(R.string.you_lost);
                    } else  if (Helpers.checkIfBoardFull(gameRoom.board)) {
                        result = getResources().getString(R.string.draw);
                    }

                    if (!result.isEmpty()) {
                        Map<String, Object> roomMap = new HashMap<String, Object>();
                        roomMap.put(roomKey, gameRoom);
                        roomsRef.updateChildren(roomMap);
                        AlertDialog.Builder alert = new AlertDialog.Builder(OnlineActivity.this);
                        alert.setTitle(getResources().getString(R.string.game_result));
                        alert.setMessage(getResources().getString(R.string.want_to_play_a_revenge, result));
                        alert.setNegativeButton(getResources().getString(R.string.no), (dialogInterface, i) -> {
                            gameRoom.setStatus(RoomStatus.FINISHED);
                            Map<String, Object> roomMap1 = new HashMap<String, Object>();
                            roomMap1.put(roomKey, gameRoom);
                            roomsRef.updateChildren(roomMap1);
                            Intent intent = new Intent(OnlineActivity.this, MainActivity.class);
                            startActivity(intent);
                        });
                        alert.setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> {
                            waitingContainer.setVisibility(View.VISIBLE);
                            if (gameRoom.status == RoomStatus.WAITING_FOR_REVENGE) {
                                gameRoom.setStatus(RoomStatus.WAITING);
                            } else {
                                gameRoom.setStatus(RoomStatus.WAITING_FOR_REVENGE);
                            }
                            gameRoom.setBoard(new ArrayList<Integer>(Collections.nCopies(9, 0)));
                            Map<String, Object> roomMap12 = new HashMap<String, Object>();
                            roomMap12.put(roomKey, gameRoom);
                            roomsRef.updateChildren(roomMap12);
                        });
                        alert.show();
                    }
                } else if (gameRoom.status == RoomStatus.FINISHED) {
                    roomRef.setValue(null);
                    playersRef.child(playerKey).setValue(null);
                    Intent intent = new Intent(OnlineActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fixStartingPlayer() {
        roomRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                DataSnapshot snapshot = task.getResult();
                Room room = snapshot.getValue(Room.class);
                if (room.currentPlayer.equals(playerKey)) {
                    if (currentPlayer == 1) {
                        player1Text.setTextColor(ContextCompat.getColor(OnlineActivity.this, R.color.white));
                    } else {
                        player2Text.setTextColor(ContextCompat.getColor(OnlineActivity.this, R.color.white));
                    }
                } else {
                    if (currentPlayer == 1) {
                        player2Text.setTextColor(ContextCompat.getColor(OnlineActivity.this, R.color.white));
                    } else {
                        player1Text.setTextColor(ContextCompat.getColor(OnlineActivity.this, R.color.white));
                    }
                }
            }
        });
    }

    private void cellClicked(ImageView tile, int i) {
        if (gameRoom.currentPlayer.equals(playerKey) && gameRoom.board.get(i) == 0) {
            gameRoom.board.set(i, currentPlayer);
            gameRoom.setCurrentPlayer(enemyKey);
            Map<String, Object> roomMap = new HashMap<String, Object>();
            roomMap.put(roomKey, gameRoom);
            roomsRef.updateChildren(roomMap);
            tile.setBackgroundResource((currentPlayer == 1 ? R.drawable.cross : R.drawable.circle));
            if (currentPlayer == 1) {
                player1Text.setTextColor(ContextCompat.getColor(OnlineActivity.this, R.color.dark_grey));
                player2Text.setTextColor(ContextCompat.getColor(OnlineActivity.this, R.color.white));
            } else {
                player1Text.setTextColor(ContextCompat.getColor(OnlineActivity.this, R.color.white));
                player2Text.setTextColor(ContextCompat.getColor(OnlineActivity.this, R.color.dark_grey));
            }
        }
    }

    private void updateTiles() {
        for (int i = 0; i < tiles.size(); i++) {
            ImageView tile = tiles.get(i);
            if (gameRoom.board.get(i) == 1) {
                tile.setBackgroundResource(R.drawable.cross);
            } else if (gameRoom.board.get(i) == 2) {
                tile.setBackgroundResource(R.drawable.circle);
            } else {
                tile.setBackgroundResource(0);
            }
        }
    }

    private void startInterval() {
        final Handler interval = new Handler();
        interval.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                long now = new Date().getTime();
                long diff = now - currentTime;
                if (diff >= timeout && !waitingAlertShown) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(OnlineActivity.this);
                    alert.setTitle(getResources().getString(R.string.cant_find_a_game));
                    alert.setMessage(getResources().getString(R.string.would_like_to_play_offline));
                    alert.setNegativeButton(getResources().getString(R.string.no), (dialogInterface, i) -> {
                        currentTime = now;
                        waitingAlertShown = false;
                    });
                    alert.setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> {
                        shouldRunAgain = false;
                        waitingAlertShown = false;
                        playersRef.child(playerKey).setValue(null);
                        roomRef.setValue(null);
                        Intent intent = new Intent(OnlineActivity.this, OfflineActivity.class);
                        startActivity(intent);
                    });
                    waitingAlertShown = true;
                    waitingAlert = alert.create();
                    waitingAlert.show();
                }

                if (shouldRunAgain) {
                    interval.postDelayed(this, 500);
                }
            }
        }, 500);
    }
}

