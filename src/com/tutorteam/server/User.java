package com.tutorteam.server;

import com.tutorteam.server.room.Room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

final public class User extends Thread {

    private BufferedReader reader;
    private PrintWriter writer;
    private String userName;
    private int score;
    private boolean isAlive;
    private Room room;

    User(Socket newConnection, Room room) throws IOException {
        reader = new BufferedReader(new InputStreamReader(newConnection.getInputStream()));
        writer = new PrintWriter(newConnection.getOutputStream(), true);
        score = 0;
        isAlive = true;
        this.room = room;
    }

    public int getScore() {
        return score;
    }

    @Override
    public void run() {
        try {
            writer.println("Welcome To Asteroids Server");
            writer.println("Please, introduce yourself!");
            userName = reader.readLine();
            writer.println("You need to keep a space garbage.");
            writer.println("You need to keep a space garbage.");
            writer.println("Good luck, Commander!");
            room.addUser(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s %d", userName, score);
    }

    public String getUserName() {
        return userName;
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void addScore() {
        if (room.isGameStarted()) score += 10;
    }

    public void substractScore() {
        if (room.isGameStarted()) {
            score -= 50;
            if (score < 0) isAlive = false;
        }
    }

    public boolean getIsAlive() {
        return isAlive;
    }

    public void died() {
        isAlive = false;
        this.sendMessage("You are died!");
        String scoreMessage = String.format("You collect %d score", this.score);
        this.sendMessage(scoreMessage);
    }
}