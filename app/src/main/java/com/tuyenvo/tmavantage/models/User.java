package com.tuyenvo.tmavantage.models;

public class User {
    private String username, password, token;
    public User(){

    }

    public User(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }
}
