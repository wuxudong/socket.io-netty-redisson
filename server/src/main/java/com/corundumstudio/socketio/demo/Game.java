package com.corundumstudio.socketio.demo;

public class Game {
    int id;
    String user;
    boolean result;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Game() {
    }

    public Game(int id, String user, boolean result) {
        this.id = id;
        this.user = user;
        this.result = result;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", user='" + user + '\'' +
                ", result=" + result +
                '}';
    }
}
