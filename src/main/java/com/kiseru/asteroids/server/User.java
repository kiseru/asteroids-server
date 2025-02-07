package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.logics.models.Spaceship;

public class User {

    private final int id;
    private final String username;

    private int score = 100;
    private int steps = 0;

    private boolean isAlive = true;
    private Spaceship spaceship;

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

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

    public void addScore() {
        score += 10;
    }

    public void subtractScore() {
        score -= 50;
        isAlive = score >= 0;
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
