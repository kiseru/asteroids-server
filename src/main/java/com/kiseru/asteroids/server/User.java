package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.logics.auxiliary.Direction;
import com.kiseru.asteroids.server.logics.models.Spaceship;
import com.kiseru.asteroids.server.room.Room;
import java.util.Random;
import java.util.function.Consumer;

public class User {

    private final Room room;
    private final Consumer<String> onSendMessage;
    private final int id = new Random().nextInt(100);

    private String username;
    private int score = 100;
    private int steps = 0;

    private boolean isAlive = true;
    private Spaceship spaceship;

    public User(Room room, Consumer<String> onSendMessage) {
        this.room = room;
        this.onSendMessage = onSendMessage;
    }

    public void moveSpaceshipForward() {
        spaceship.go();
        room.getGame().refresh();
    }

    public void updateSpaceshipDirection(Direction direction) {
        spaceship.setDirection(direction);
        room.getGame().refresh();
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void sendMessage(String message) {
        onSendMessage.accept(message);
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

    public Spaceship getSpaceship() {
        return spaceship;
    }

    public void setSpaceship(Spaceship spaceship) {
        this.spaceship = spaceship;
    }

    public Room getRoom() {
        return room;
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
