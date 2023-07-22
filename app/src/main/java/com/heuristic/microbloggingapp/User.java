package com.heuristic.microbloggingapp;

public class User {

    private String userId, username;

    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;

    }

    public User() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
