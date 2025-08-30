package com.example.fingerjam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.text.TextUtils;

import com.example.fingerjam.multiplayer.MultiplayerManager;
import com.example.fingerjam.models.GameRoom;
import com.example.fingerjam.models.Player;

import java.util.ArrayList;
import java.util.List;

public class MultiplayerMenuActivity extends AppCompatActivity implements MultiplayerManager.MultiplayerCallback {
    
    private Button createRoomButton;
    private Button joinRoomButton;
    private Button refreshRoomsButton;
    private Button backButton;
    private LinearLayout mainLayout;
    private TextView titleText;
    private RecyclerView roomsRecyclerView;
    private RoomsAdapter roomsAdapter;
    private List<GameRoom> availableRooms;
    
    private MultiplayerManager multiplayerManager;
    private ThemeManager themeManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_menu);
        
        multiplayerManager = MultiplayerManager.getInstance(this);
        multiplayerManager.setCallback(this);
        
        themeManager = ThemeManager.getInstance(this);
        
        initializeFields();
        applyCurrentTheme();
        setupButtons();
        loadAvailableRooms();
    }
    
    private void initializeFields() {
        createRoomButton = findViewById(R.id.createRoomButton);
        joinRoomButton = findViewById(R.id.joinRoomButton);
        refreshRoomsButton = findViewById(R.id.refreshRoomsButton);
        backButton = findViewById(R.id.backButton);
        mainLayout = findViewById(R.id.mainLayout);
        titleText = findViewById(R.id.titleText);
        roomsRecyclerView = findViewById(R.id.roomsRecyclerView);
        
        availableRooms = new ArrayList<>();
        roomsAdapter = new RoomsAdapter(availableRooms, this::onRoomSelected);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomsRecyclerView.setAdapter(roomsAdapter);
    }
    
    private void applyCurrentTheme() {
        mainLayout.setBackgroundResource(themeManager.getBackgroundDrawable());
        themeManager.applyThemeToView(createRoomButton);
        themeManager.applyThemeToView(joinRoomButton);
        themeManager.applyThemeToView(refreshRoomsButton);
        themeManager.applyThemeToView(backButton);
        themeManager.applyThemeToTextView(titleText, true);
    }
    
    private void setupButtons() {
        createRoomButton.setOnClickListener(v -> showCreateRoomDialog());
        joinRoomButton.setOnClickListener(v -> showJoinRoomDialog());
        refreshRoomsButton.setOnClickListener(v -> loadAvailableRooms());
        backButton.setOnClickListener(v -> onBackPressed());
    }
    
    private void showCreateRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_room, null);
        
        EditText roomNameInput = dialogView.findViewById(R.id.roomNameInput);
        EditText playerNameInput = dialogView.findViewById(R.id.playerNameInput);
        EditText targetValueInput = dialogView.findViewById(R.id.targetValueInput);
        TextView gameTypeLabel = dialogView.findViewById(R.id.gameTypeLabel);
        
        // Set default values
        playerNameInput.setText("Player" + System.currentTimeMillis() % 1000);
        targetValueInput.setText("100");
        
        builder.setView(dialogView)
               .setTitle("Create Multiplayer Room")
               .setPositiveButton("Create", (dialog, which) -> {
                   String roomName = roomNameInput.getText().toString();
                   String playerName = playerNameInput.getText().toString();
                   String targetValueStr = targetValueInput.getText().toString();
                   
                   if (TextUtils.isEmpty(roomName) || TextUtils.isEmpty(playerName) || TextUtils.isEmpty(targetValueStr)) {
                       Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                       return;
                   }
                   
                   int targetValue = Integer.parseInt(targetValueStr);
                   if (targetValue <= 0) {
                       Toast.makeText(this, "Target value must be positive", Toast.LENGTH_SHORT).show();
                       return;
                   }
                   
                   // Create room (default to touches game type)
                   multiplayerManager.createRoom(roomName, "touches", targetValue, playerName);
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    private void showJoinRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_join_room, null);
        
        EditText roomIdInput = dialogView.findViewById(R.id.roomIdInput);
        EditText playerNameInput = dialogView.findViewById(R.id.playerNameInput);
        
        // Set default player name
        playerNameInput.setText("Player" + System.currentTimeMillis() % 1000);
        
        builder.setView(dialogView)
               .setTitle("Join Room")
               .setPositiveButton("Join", (dialog, which) -> {
                   String roomId = roomIdInput.getText().toString();
                   String playerName = playerNameInput.getText().toString();
                   
                   if (TextUtils.isEmpty(roomId) || TextUtils.isEmpty(playerName)) {
                       Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                       return;
                   }
                   
                   multiplayerManager.joinRoom(roomId, playerName);
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    private void loadAvailableRooms() {
        // This would typically fetch from Firebase
        // For now, we'll show a message
        Toast.makeText(this, "Refreshing rooms...", Toast.LENGTH_SHORT).show();
    }
    
    private void onRoomSelected(GameRoom room) {
        // Show room details and join option
        showRoomDetailsDialog(room);
    }
    
    private void showRoomDetailsDialog(GameRoom room) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Room: " + room.getRoomName())
               .setMessage("Game Type: " + room.getGameType() + "\n" +
                          "Target: " + room.getTargetValue() + "\n" +
                          "Players: " + room.getPlayers().size() + "/" + room.getMaxPlayers() + "\n" +
                          "Status: " + room.getStatus())
               .setPositiveButton("Join", (dialog, which) -> {
                   // Join the selected room
                   String playerName = "Player" + System.currentTimeMillis() % 1000;
                   multiplayerManager.joinRoom(room.getRoomId(), playerName);
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    // MultiplayerManager callbacks
    @Override
    public void onRoomCreated(GameRoom room) {
        Toast.makeText(this, "Room created successfully!", Toast.LENGTH_SHORT).show();
        // Navigate to room lobby
        Intent intent = new Intent(this, MultiplayerLobbyActivity.class);
        intent.putExtra("roomId", room.getRoomId());
        startActivity(intent);
    }
    
    @Override
    public void onRoomJoined(GameRoom room) {
        Toast.makeText(this, "Joined room: " + room.getRoomName(), Toast.LENGTH_SHORT).show();
        // Navigate to room lobby
        Intent intent = new Intent(this, MultiplayerLobbyActivity.class);
        intent.putExtra("roomId", room.getRoomId());
        startActivity(intent);
    }
    
    @Override
    public void onPlayerJoined(Player player) {
        // Handle in lobby activity
    }
    
    @Override
    public void onPlayerLeft(Player player) {
        // Handle in lobby activity
    }
    
    @Override
    public void onGameStarted() {
        // Handle in lobby activity
    }
    
    @Override
    public void onGameEnded(List<Player> leaderboard) {
        // Handle in lobby activity
    }
    
    @Override
    public void onPlayerUpdated(Player player) {
        // Handle in lobby activity
    }
    
    @Override
    public void onCountdownStarted() {
        // Handle in lobby activity
    }
    
    @Override
    public void onError(String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
    }
    
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PracticeMenuActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (multiplayerManager != null) {
            multiplayerManager.cleanup();
        }
    }
} 