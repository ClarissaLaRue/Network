package com.client.json;

public class UserInfo {
    private int id;
    private String userName;
    private boolean online;
    private int token;

    public UserInfo(int id, String userName, boolean online) {
        this.id = id;
        this.userName = userName;
        this.online = online;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isOnlain() {
        return online;
    }

    public int getToken() {
        return token;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setOnlain(boolean online) {
        this.online = online;
    }

    public void setToken(int token) {
        this.token = token;
    }
}
