package com.example.fingerjam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fingerjam.multiplayer.MultiplayerManager;
import com.example.fingerjam.models.GameRoom;
import com.example.fingerjam.models.Player;

import java.util.List;

public class MultiplayerGameActivity extends AppCompatActivity implements MultiplayerManager.MultiplayerCallback {
    
    private TextView touchCountText;
    private TextView timerText;
    private TextView gameInfoText;
    private TextView statusText;
    private LinearLayout gameArea;
    private Button leaveGameButton;
    private LinearLayout mainLayout;
    
    private MultiplayerManager multiplayerManager;
    private ThemeManager themeManager;
    private String roomId;
    private GameRoom currentRoom;
    private Player currentPlayer;
    
    private int touchCount = 0;
    private long startTime = 0;
    private boolean gameStarted = false;
    private boolean gameEnded = false;
    private Handler gameHandler = new Handler();
    private Runnable gameTimer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_game);
        
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
        setupGameArea();
        setupGameTimer();
    }
    
    private void initializeFields() {
        touchCountText = findViewById(R.id.touchCountText);
        timerText = findViewById(R.id.timerText);
        gameInfoText = findViewById(R.id.gameInfoText);
        statusText = findViewById(R.id.statusText);
        gameArea = findViewById(R.id.gameArea);
        leaveGameButton = findViewById(R.id.leaveGameButton);
        mainLayout = findViewById(R.id.mainLayout);
    }
    
    private void applyCurrentTheme() {
        mainLayout.setBackgroundResource(themeManager.getBackgroundDrawable());
        themeManager.applyThemeToView(leaveGameButton);
        themeManager.applyThemeToTextView(touchCountText, true);
        themeManager.applyThemeToTextView(timerText, true);
        themeManager.applyThemeToTextView(gameInfoText, true);
        themeManager.applyThemeToTextView(statusText, true);
    }
    
    private void setupGameArea() {
        gameArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && gameStarted && !gameEnded) {
                    handleTouch();
                    return true;
                }
                return false;
            }
        });
        
        leaveGameButton.setOnClickListener(v -> leaveGame());
    }
    
    private void setupGameTimer() {
        gameTimer = new Runnable() {
            @Override
            public void run() {
                if (gameStarted && !gameEnded) {
                    updateTimer();
                    gameHandler.postDelayed(this, 100);
                }
            }
        };
    }
    
    private void handleTouch() {
        touchCount++;
        touchCountText.setText("Touches: " + touchCount);
        
        // Update touch count in Firebase
        multiplayerManager.updatePlayerTouches(touchCount);
        
        // Check if target reached
        if (currentRoom != null && touchCount >= currentRoom.getTargetValue()) {
            endGame();
        }
    }
    
    private void updateTimer() {
        if (startTime > 0) {
            long elapsedTime = SystemClock.uptimeMillis() - startTime;
            long seconds = elapsedTime / 1000;
            long minutes = seconds / 60;
            seconds %= 60;
            long milliseconds = (elapsedTime % 1000) / 100;
            
            timerText.setText(String.format("Time: %02d:%02d.%01d", minutes, seconds, milliseconds));
        }
    }
    
    private void startGame() {
        gameStarted = true;
        startTime = SystemClock.uptimeMillis();
        statusText.setText("Game in progress...");
        gameHandler.post(gameTimer);
        
        // Update player status
        if (currentPlayer != null) {
            currentPlayer.setStatus("playing");
            currentPlayer.setStartTime(startTime);
        }
    }
    
    private void endGame() {
        gameEnded = true;
        gameStarted = false;
        statusText.setText("Game finished!");
        
        // Calculate final score
        if (currentPlayer != null && startTime > 0) {
            long currentTime = SystemClock.uptimeMillis();
            currentPlayer.updateScore(currentTime);
        }
        
        // Stop timer
        gameHandler.removeCallbacks(gameTimer);
        
        // Show results after a delay
        new Handler().postDelayed(() -> {
            showGameResults();
        }, 2000);
    }
    
    private void leaveGame() {
        if (gameStarted && !gameEnded) {
            // Player left during game
            Toast.makeText(this, "You left the game", Toast.LENGTH_SHORT).show();
        }
        
        multiplayerManager.leaveRoom();
        Intent intent = new Intent(this, MultiplayerMenuActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void showGameResults() {
        // This will be handled by the callback when the game ends
        // For now, just show a message
        Toast.makeText(this, "Game finished! Returning to lobby...", Toast.LENGTH_LONG).show();
        
        // Return to lobby
        Intent intent = new Intent(this, MultiplayerLobbyActivity.class);
        intent.putExtra("roomId", roomId);
        startActivity(intent);
        finish();
    }
    
    private void updateGameInfo() {
        if (currentRoom != null) {
            gameInfoText.setText("Target: " + currentRoom.getTargetValue() + " touches | " +
                               "Players: " + currentRoom.getPlayers().size());
        }
    }
    
    // MultiplayerManager callbacks
    @Override
    public void onRoomCreated(GameRoom room) {
        // Not used in game
    }
    
    @Override
    public void onRoomJoined(GameRoom room) {
        currentRoom = room;
        currentPlayer = room.getPlayers().get(multiplayerManager.getCurrentPlayerId());
        updateGameInfo();
    }
    
    @Override
    public void onPlayerJoined(Player player) {
        // Update game info
        updateGameInfo();
    }
    
    @Override
    public void onPlayerLeft(Player player) {
        // Update game info
        updateGameInfo();
        
        // If too many players left, end game
        if (currentRoom != null && currentRoom.getPlayers().size() < 2) {
            Toast.makeText(this, "Not enough players. Game ending...", Toast.LENGTH_SHORT).show();
            endGame();
        }
    }
    
    @Override
    public void onGameStarted() {
        // Game started by host
        startGame();
    }
    
    @Override
    public void onGameEnded(List<Player> leaderboard) {
        // Game ended by host
        endGame();
    }
    
    @Override
    public void onPlayerUpdated(Player player) {
        // Update current player if it's us
        if (player.getId().equals(multiplayerManager.getCurrentPlayerId())) {
            currentPlayer = player;
        }
        
        // Update game info
        updateGameInfo();
    }
    
    @Override
    public void onCountdownStarted() {
        // Countdown started, prepare for game
        statusText.setText("Get ready...");
    }
    
    @Override
    public void onError(String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameHandler != null) {
            gameHandler.removeCallbacks(gameTimer);
        }
    }
} 