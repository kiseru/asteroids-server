package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.logics.models.Spaceship;

public class User {

    private final int id;
    private final String username;
    private Spaceship spaceship;

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("%s %d", username, spaceship.getScore());
    }

    public String getUsername() {
        return username;
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
}
