package com.example.fingerjam.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GameRoom implements Serializable {
    private String roomId;
    private String roomName;
    private String hostId;
    private String gameType; // "touches" or "time"
    private int targetValue; // target touches or seconds
    private String status; // "waiting", "countdown", "playing", "finished"
    private long gameStartTime;
    private long countdownStartTime;
    private int maxPlayers;
    private boolean isPrivate;
    private String password;
    private Map<String, Player> players;
    private List<String> playerIds;
    private long createdAt;
    
    public GameRoom() {
        // Required for Firebase
        this.players = new HashMap<>();
        this.playerIds = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
    }
    
    public GameRoom(String roomId, String roomName, String hostId, String gameType, int targetValue) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.hostId = hostId;
        this.gameType = gameType;
        this.targetValue = targetValue;
        this.status = "waiting";
        this.gameStartTime = 0;
        this.countdownStartTime = 0;
        this.maxPlayers = 4;
        this.isPrivate = false;
        this.password = "";
        this.players = new HashMap<>();
        this.playerIds = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    
    public String getHostId() { return hostId; }
    public void setHostId(String hostId) { this.hostId = hostId; }
    
    public String getGameType() { return gameType; }
    public void setGameType(String gameType) { this.gameType = gameType; }
    
    public int getTargetValue() { return targetValue; }
    public void setTargetValue(int targetValue) { this.targetValue = targetValue; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public long getGameStartTime() { return gameStartTime; }
    public void setGameStartTime(long gameStartTime) { this.gameStartTime = gameStartTime; }
    
    public long getCountdownStartTime() { return countdownStartTime; }
    public void setCountdownStartTime(long countdownStartTime) { this.countdownStartTime = countdownStartTime; }
    
    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
    
    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean aPrivate) { isPrivate = aPrivate; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Map<String, Player> getPlayers() { return players; }
    public void setPlayers(Map<String, Player> players) { this.players = players; }
    
    public List<String> getPlayerIds() { return playerIds; }
    public void setPlayerIds(List<String> playerIds) { this.playerIds = playerIds; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    // Helper methods
    public void addPlayer(Player player) {
        if (players.size() < maxPlayers) {
            players.put(player.getId(), player);
            if (!playerIds.contains(player.getId())) {
                playerIds.add(player.getId());
            }
        }
    }
    
    public void removePlayer(String playerId) {
        players.remove(playerId);
        playerIds.remove(playerId);
        
        // If host leaves, assign new host
        if (hostId.equals(playerId) && !players.isEmpty()) {
            String newHostId = playerIds.get(0);
            Player newHost = players.get(newHostId);
            if (newHost != null) {
                newHost.setHost(true);
                this.hostId = newHostId;
            }
        }
    }
    
    public boolean isFull() {
        return players.size() >= maxPlayers;
    }
    
    public boolean canStart() {
        if (players.size() < 2) return false;
        
        // Check if all players are ready
        for (Player player : players.values()) {
            if (!player.isReady()) return false;
        }
        return true;
    }
    
    public void startCountdown() {
        this.status = "countdown";
        this.countdownStartTime = System.currentTimeMillis();
    }
    
    public void startGame() {
        this.status = "playing";
        this.gameStartTime = System.currentTimeMillis();
        
        // Set all players to playing status
        for (Player player : players.values()) {
            player.setStatus("playing");
            player.setStartTime(this.gameStartTime);
        }
    }
    
    public void endGame() {
        this.status = "finished";
        
        // Calculate final scores for all players
        long currentTime = System.currentTimeMillis();
        for (Player player : players.values()) {
            player.updateScore(currentTime);
            player.setStatus("finished");
        }
    }
    
    public List<Player> getLeaderboard() {
        List<Player> leaderboard = new ArrayList<>(players.values());
        leaderboard.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
        return leaderboard;
    }
} 