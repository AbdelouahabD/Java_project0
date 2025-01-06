package com.example.controllers;

public class GlobalState {
    private static GlobalState instance;
    private int userId; 
    private GlobalState() { }

    public static GlobalState getInstance() {
        if (instance == null) {
            instance = new GlobalState();
        }
        return instance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
