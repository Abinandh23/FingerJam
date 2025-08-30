package com.example.fingerjam.models;

import java.io.Serializable;

public class Player implements Serializable {
    private String id;
    private String name;
    private int score;
    private int touches;
    private long startTime;
    private boolean isReady;
    private boolean isHost;
    private String status; // "waiting", "playing", "finished"
    
    public Player() {
        // Required for Firebase
    }
    
    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.score = 0;
        this.touches = 0;
        this.startTime = 0;
        this.isReady = false;
        this.isHost = false;
        this.status = "waiting";
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public int getTouches() { return touches; }
    public void setTouches(int touches) { this.touches = touches; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    public boolean isReady() { return isReady; }
    public void setReady(boolean ready) { isReady = ready; }
    
    public boolean isHost() { return isHost; }
    public void setHost(boolean host) { isHost = host; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public void incrementTouches() {
        this.touches++;
    }
    
    public void updateScore(long currentTime) {
        if (startTime > 0) {
            long timeElapsed = currentTime - startTime;
            // Score calculation: higher touches, lower time = higher score
            this.score = (touches * 1000) - (int)(timeElapsed / 100);
        }
    }
} 