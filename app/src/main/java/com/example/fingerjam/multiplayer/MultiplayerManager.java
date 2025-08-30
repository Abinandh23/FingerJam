package com.example.fingerjam.multiplayer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.fingerjam.models.GameRoom;
import com.example.fingerjam.models.Player;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ChildEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MultiplayerManager {
    private static final String TAG = "MultiplayerManager";
    
    private static MultiplayerManager instance;
    private Context context;
    private FirebaseDatabase database;
    private DatabaseReference roomsRef;
    private DatabaseReference playersRef;
    
    private MultiplayerCallback callback;
    private String currentPlayerId;
    private String currentRoomId;
    private Player currentPlayer;
    
    public interface MultiplayerCallback {
        void onRoomCreated(GameRoom room);
        void onRoomJoined(GameRoom room);
        void onPlayerJoined(Player player);
        void onPlayerLeft(Player player);
        void onGameStarted();
        void onGameEnded(List<Player> leaderboard);
        void onPlayerUpdated(Player player);
        void onCountdownStarted();
        void onError(String error);
    }
    
    private MultiplayerManager(Context context) {
        this.context = context;
        this.database = FirebaseDatabase.getInstance();
        this.roomsRef = database.getReference("rooms");
        this.playersRef = database.getReference("players");
        
        // Generate unique player ID
        this.currentPlayerId = UUID.randomUUID().toString();
    }
    
    public static MultiplayerManager getInstance(Context context) {
        if (instance == null) {
            instance = new MultiplayerManager(context);
        }
        return instance;
    }
    
    public void setCallback(MultiplayerCallback callback) {
        this.callback = callback;
    }
    
    public String getCurrentPlayerId() {
        return currentPlayerId;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public String getCurrentRoomId() {
        return currentRoomId;
    }
    
    // Create a new room
    public void createRoom(String roomName, String gameType, int targetValue, String playerName) {
        String roomId = UUID.randomUUID().toString();
        GameRoom room = new GameRoom(roomId, roomName, currentPlayerId, gameType, targetValue);
        
        // Create current player
        currentPlayer = new Player(currentPlayerId, playerName);
        currentPlayer.setHost(true);
        currentPlayer.setReady(true);
        
        // Add player to room
        room.addPlayer(currentPlayer);
        
        // Save room to Firebase
        roomsRef.child(roomId).setValue(room)
            .addOnSuccessListener(aVoid -> {
                currentRoomId = roomId;
                if (callback != null) {
                    callback.onRoomCreated(room);
                }
                startRoomListener(roomId);
            })
            .addOnFailureListener(e -> {
                if (callback != null) {
                    callback.onError("Failed to create room: " + e.getMessage());
                }
            });
    }
    
    // Join an existing room
    public void joinRoom(String roomId, String playerName) {
        currentPlayer = new Player(currentPlayerId, playerName);
        currentRoomId = roomId;
        
        // Add player to room
        roomsRef.child(roomId).child("players").child(currentPlayerId).setValue(currentPlayer)
            .addOnSuccessListener(aVoid -> {
                startRoomListener(roomId);
            })
            .addOnFailureListener(e -> {
                if (callback != null) {
                    callback.onError("Failed to join room: " + e.getMessage());
                }
            });
    }
    
    // Leave current room
    public void leaveRoom() {
        if (currentRoomId != null && currentPlayerId != null) {
            roomsRef.child(currentRoomId).child("players").child(currentPlayerId).removeValue();
            currentRoomId = null;
            currentPlayer = null;
        }
    }
    
    // Set player ready status
    public void setPlayerReady(boolean ready) {
        if (currentRoomId != null && currentPlayerId != null) {
            roomsRef.child(currentRoomId).child("players").child(currentPlayerId).child("ready").setValue(ready);
        }
    }
    
    // Start countdown (host only)
    public void startCountdown() {
        if (currentRoomId != null && currentPlayer != null && currentPlayer.isHost()) {
            roomsRef.child(currentRoomId).child("status").setValue("countdown");
            roomsRef.child(currentRoomId).child("countdownStartTime").setValue(System.currentTimeMillis());
        }
    }
    
    // Start game (host only)
    public void startGame() {
        if (currentRoomId != null && currentPlayer != null && currentPlayer.isHost()) {
            roomsRef.child(currentRoomId).child("status").setValue("playing");
            roomsRef.child(currentRoomId).child("gameStartTime").setValue(System.currentTimeMillis());
        }
    }
    
    // Update player touches
    public void updatePlayerTouches(int touches) {
        if (currentRoomId != null && currentPlayerId != null) {
            roomsRef.child(currentRoomId).child("players").child(currentPlayerId).child("touches").setValue(touches);
        }
    }
    
    // End game (host only)
    public void endGame() {
        if (currentRoomId != null && currentPlayer != null && currentPlayer.isHost()) {
            roomsRef.child(currentRoomId).child("status").setValue("finished");
        }
    }
    
    // Get available rooms
    public void getAvailableRooms() {
        roomsRef.orderByChild("status").equalTo("waiting").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<GameRoom> availableRooms = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GameRoom room = snapshot.getValue(GameRoom.class);
                    if (room != null && !room.isFull()) {
                        availableRooms.add(room);
                    }
                }
                // You can add a callback method for this if needed
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error getting available rooms", databaseError.toException());
            }
        });
    }
    
    // Start listening to room updates
    private void startRoomListener(String roomId) {
        roomsRef.child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GameRoom room = dataSnapshot.getValue(GameRoom.class);
                if (room != null) {
                    handleRoomUpdate(room);
                }
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error listening to room", databaseError.toException());
            }
        });
    }
    
    // Handle room updates
    private void handleRoomUpdate(GameRoom room) {
        if (callback == null) return;
        
        switch (room.getStatus()) {
            case "countdown":
                callback.onCountdownStarted();
                break;
            case "playing":
                callback.onGameStarted();
                break;
            case "finished":
                callback.onGameEnded(room.getLeaderboard());
                break;
        }
        
        // Check for player updates
        if (currentPlayer != null) {
            Player updatedPlayer = room.getPlayers().get(currentPlayerId);
            if (updatedPlayer != null) {
                currentPlayer = updatedPlayer;
            }
        }
    }
    
    // Clean up
    public void cleanup() {
        if (currentRoomId != null) {
            leaveRoom();
        }
        if (instance != null) {
            instance = null;
        }
    }
} 