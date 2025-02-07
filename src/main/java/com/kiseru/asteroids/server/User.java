package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.logics.models.Spaceship;
import com.kiseru.asteroids.server.room.Room;
import com.kiseru.asteroids.server.room.RoomStatus;
import java.util.Random;

public class User {

    private final int id = new Random().nextInt(100);

    private String username;
    private int score = 100;
    private int steps = 0;

    private boolean isAlive = true;
    private Spaceship spaceship;

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return String.format("%s %d", username, score);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addScore(Room room) {
        if (room.getStatus() == RoomStatus.GAMING) {
            score += 10;
        }
    }

    public void subtractScore(Room room) {
        if (room.getStatus() == RoomStatus.GAMING) {
            score -= 50;
            if (score < 0) isAlive = false;
        }
    }

    public Spaceship getSpaceship() {
        return spaceship;
    }

    public void setSpaceship(Spaceship spaceship) {
        this.spaceship = spaceship;
    }

    public int getId() {
        return id;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
