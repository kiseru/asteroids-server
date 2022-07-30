package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.model.Direction;
import com.kiseru.asteroids.server.model.Room;
import com.kiseru.asteroids.server.model.SpaceShip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public final class User implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(User.class);

    private static int nextId = 0;

    private final BufferedReader reader;

    private final PrintWriter writer;

    private final Room room;

    private final int id;

    private final String username;

    private int score = 100;

    private int steps = 0;

    private boolean isAlive = true;

    private SpaceShip spaceShip;

    public User(String username, Room room, BufferedReader reader, PrintWriter writer) {
        this.username = username;
        this.room = room;
        this.reader = reader;
        this.writer = writer;
        this.id = nextId++;
    }

    public int getScore() {
        return score;
    }

    @Override
    public void run() {
        init();
        try {
            while (!room.isGameFinished() && isAlive) {
                String command = reader.readLine();
                handleCommand(command);
                incrementSteps();
            }
        } catch (IOException e) {
            log.error("Connection problems with user " + username, e);
        } finally {
            isAlive = false;
            room.setGameFinished();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        writer.println(message);
        writer.flush();
    }

    public void addScore() {
        if (room.isGameStarted()) {
            score += 10;
        }
    }

    public void subtractScore() {
        if (room.isGameStarted()) {
            score -= 50;
            if (score < 0) isAlive = false;
        }
    }

    public void died() {
        isAlive = false;
        sendGameOverMessage();
    }

    public void checkCollectedGarbage(int collected) {
        room.checkCollectedGarbage(collected);
    }

    public Room getRoom() {
        return room;
    }

    public SpaceShip getSpaceShip() {
        return spaceShip;
    }

    public void setSpaceShip(SpaceShip spaceShip) {
        this.spaceShip = spaceShip;
    }

    public int getId() {
        return id;
    }

    public boolean isAlive() {
        return isAlive;
    }

    private void sendGameOverMessage() {
        writer.println("died");
        writer.println(String.format("You have collected %d score", score));
        writer.flush();
    }

    private void init() {
        room.addUserToRoom(this);
        spaceShip.setDirection(Direction.UP);
    }

    private void handleCommand(String command) {
        switch (command) {
            case "go" -> handleGo();
            case "left" -> handleLeft();
            case "right" -> handleRight();
            case "up" -> handleUp();
            case "down" -> handleDown();
            case "isAsteroid" -> handleIsAsteroid();
            case "isGarbage" -> handleIsGarbage();
            case "isWall" -> handleIsWall();
            default -> sendMessage("Unknown command");
        }
    }

    private void handleGo() {
        spaceShip.go();
        room.refresh();
        sendMessage(Integer.toString(score));
    }

    private void handleLeft() {
        handleDirection(Direction.LEFT);
    }

    private void handleRight() {
        handleDirection(Direction.RIGHT);
    }

    private void handleUp() {
        handleDirection(Direction.UP);
    }

    private void handleDown() {
        handleDirection(Direction.DOWN);
    }

    private void handleDirection(Direction direction) {
        spaceShip.setDirection(direction);
        room.refresh();
        sendMessage("success");
    }

    private void handleIsAsteroid() {
        sendMessage(spaceShip.getCourseChecker().isAsteroid() ? "t" : "f");
    }

    private void handleIsGarbage() {
        sendMessage(spaceShip.getCourseChecker().isGarbage() ? "t" : "f");
    }

    private void handleIsWall() {
        sendMessage(spaceShip.getCourseChecker().isWall() ? "t" : "f");
    }

    private void incrementSteps() {
        steps++;
        if (steps >= 1500 || score < 0) {
            died();
        }
    }
}
