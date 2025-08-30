package com.example.fingerjam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fingerjam.multiplayer.MultiplayerManager;
import com.example.fingerjam.models.GameRoom;
import com.example.fingerjam.models.Player;

import java.util.ArrayList;
import java.util.List;

public class MultiplayerLobbyActivity extends AppCompatActivity implements MultiplayerManager.MultiplayerCallback {
    
    private TextView roomNameText;
    private TextView gameInfoText;
    private TextView statusText;
    private TextView countdownText;
    private Button readyButton;
    private Button startGameButton;
    private Button leaveRoomButton;
    private LinearLayout mainLayout;
    private RecyclerView playersRecyclerView;
    private PlayersAdapter playersAdapter;
    private List<Player> players;
    
    private MultiplayerManager multiplayerManager;
    private ThemeManager themeManager;
    private String roomId;
    private GameRoom currentRoom;
    private Player currentPlayer;
    private CountDownTimer countdownTimer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_lobby);
        
        roomId = getIntent().getStringExtra("roomId");
        if (roomId == null) {
            Toast.makeText(this, "Error: No room ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        multiplayerManager = MultiplayerManager.getInstance(this);
        multiplayerManager.setCallback(this);
        
        themeManager = ThemeManager.getInstance(this);
        
        initializeFields();
        applyCurrentTheme();
        setupButtons();
        loadRoomData();
    }
    
    private void initializeFields() {
        roomNameText = findViewById(R.id.roomNameText);
        gameInfoText = findViewById(R.id.gameInfoText);
        statusText = findViewById(R.id.statusText);
        countdownText = findViewById(R.id.countdownText);
        readyButton = findViewById(R.id.readyButton);
        startGameButton = findViewById(R.id.startGameButton);
        leaveRoomButton = findViewById(R.id.leaveRoomButton);
        mainLayout = findViewById(R.id.mainLayout);
        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        
        players = new ArrayList<>();
        playersAdapter = new PlayersAdapter(players);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playersRecyclerView.setAdapter(playersAdapter);
    }
    
    private void applyCurrentTheme() {
        mainLayout.setBackgroundResource(themeManager.getBackgroundDrawable());
        themeManager.applyThemeToView(readyButton);
        themeManager.applyThemeToView(startGameButton);
        themeManager.applyThemeToView(leaveRoomButton);
        themeManager.applyThemeToTextView(roomNameText, true);
        themeManager.applyThemeToTextView(gameInfoText, true);
        themeManager.applyThemeToTextView(statusText, true);
        themeManager.applyThemeToTextView(countdownText, true);
    }
    
    private void setupButtons() {
        readyButton.setOnClickListener(v -> toggleReady());
        startGameButton.setOnClickListener(v -> startGame());
        leaveRoomButton.setOnClickListener(v -> leaveRoom());
        
        // Initially hide start button (only host can see it)
        startGameButton.setVisibility(View.GONE);
    }
    
    private void loadRoomData() {
        // This would typically load from Firebase
        // For now, we'll show a loading message
        statusText.setText("Loading room...");
    }
    
    private void toggleReady() {
        if (currentPlayer != null) {
            boolean newReadyState = !currentPlayer.isReady();
            multiplayerManager.setPlayerReady(newReadyState);
            
            if (newReadyState) {
                readyButton.setText("Not Ready");
                readyButton.setBackgroundResource(android.R.color.holo_red_dark);
            } else {
                readyButton.setText("Ready");
                readyButton.setBackgroundResource(android.R.color.holo_green_dark);
            }
        }
    }
    
    private void startGame() {
        if (currentPlayer != null && currentPlayer.isHost()) {
            multiplayerManager.startCountdown();
        }
    }
    
    private void leaveRoom() {
        multiplayerManager.leaveRoom();
        Intent intent = new Intent(this, MultiplayerMenuActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void updateUI() {
        if (currentRoom != null) {
            roomNameText.setText(currentRoom.getRoomName());
            gameInfoText.setText("Game Type: " + currentRoom.getGameType() + 
                               " | Target: " + currentRoom.getTargetValue());
            statusText.setText("Status: " + currentRoom.getStatus());
            
            // Update players list
            players.clear();
            players.addAll(currentRoom.getPlayers().values());
            playersAdapter.notifyDataSetChanged();
            
            // Show/hide start button based on host status
            if (currentPlayer != null && currentPlayer.isHost()) {
                startGameButton.setVisibility(View.VISIBLE);
                startGameButton.setEnabled(currentRoom.canStart());
            } else {
                startGameButton.setVisibility(View.GONE);
            }
            
            // Update ready button text
            if (currentPlayer != null) {
                if (currentPlayer.isReady()) {
                    readyButton.setText("Not Ready");
                    readyButton.setBackgroundResource(android.R.color.holo_red_dark);
                } else {
                    readyButton.setText("Ready");
                    readyButton.setBackgroundResource(android.R.color.holo_green_dark);
                }
            }
        }
    }
    
    private void startCountdown() {
        countdownText.setVisibility(View.VISIBLE);
        countdownText.setText("Game starting in 3...");
        
        countdownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                countdownText.setText("Game starting in " + secondsLeft + "...");
            }
            
            @Override
            public void onFinish() {
                countdownText.setText("GO!");
                countdownText.setVisibility(View.GONE);
                
                // Start the actual game after a short delay
                new android.os.Handler().postDelayed(() -> {
                    startMultiplayerGame();
                }, 1000);
            }
        }.start();
    }
    
    private void startMultiplayerGame() {
        // Navigate to multiplayer game screen
        Intent intent = new Intent(this, MultiplayerGameActivity.class);
        intent.putExtra("roomId", roomId);
        startActivity(intent);
        finish();
    }
    
    // MultiplayerManager callbacks
    @Override
    public void onRoomCreated(GameRoom room) {
        // Not used in lobby
    }
    
    @Override
    public void onRoomJoined(GameRoom room) {
        currentRoom = room;
        currentPlayer = room.getPlayers().get(multiplayerManager.getCurrentPlayerId());
        updateUI();
    }
    
    @Override
    public void onPlayerJoined(Player player) {
        if (currentRoom != null) {
            currentRoom.addPlayer(player);
            updateUI();
        }
    }
    
    @Override
    public void onPlayerLeft(Player player) {
        if (currentRoom != null) {
            currentRoom.removePlayer(player.getId());
            updateUI();
        }
    }
    
    @Override
    public void onGameStarted() {
        // Handle game start
        Toast.makeText(this, "Game started!", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onGameEnded(List<Player> leaderboard) {
        // Show results and return to menu
        showGameResults(leaderboard);
    }
    
    @Override
    public void onPlayerUpdated(Player player) {
        if (currentRoom != null) {
            currentRoom.getPlayers().put(player.getId(), player);
            if (player.getId().equals(multiplayerManager.getCurrentPlayerId())) {
                currentPlayer = player;
            }
            updateUI();
        }
    }
    
    @Override
    public void onCountdownStarted() {
        startCountdown();
    }
    
    @Override
    public void onError(String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
    }
    
    private void showGameResults(List<Player> leaderboard) {
        StringBuilder result = new StringBuilder("Game Results:\n\n");
        for (int i = 0; i < leaderboard.size(); i++) {
            Player player = leaderboard.get(i);
            result.append((i + 1)).append(". ").append(player.getName())
                  .append(" - Score: ").append(player.getScore())
                  .append(" (Touches: ").append(player.getTouches()).append(")\n");
        }
        
        new android.app.AlertDialog.Builder(this)
            .setTitle("Game Over!")
            .setMessage(result.toString())
            .setPositiveButton("Back to Menu", (dialog, which) -> {
                Intent intent = new Intent(this, MultiplayerMenuActivity.class);
                startActivity(intent);
                finish();
            })
            .setCancelable(false)
            .show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
    }
} 