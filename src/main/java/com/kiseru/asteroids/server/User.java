package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.handler.CommandHandlerFactory;
import com.kiseru.asteroids.server.handler.impl.CommandHandlerFactoryImpl;
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

    private static final CommandHandlerFactory commandHandlerFactory = CommandHandlerFactoryImpl.INSTANCE;

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

    public void moveSpaceship() {
        spaceShip.go();
    }

    public void refreshRoom() {
        room.refresh();
    }

    public void sendScore() {
        sendMessage(String.valueOf(score));
    }

    public void setSpaceshipDirection(Direction direction) {
        spaceShip.setDirection(direction);
    }

    public boolean isAsteroidInFrontOfSpaceship() {
        return spaceShip.isAsteroidInFrontOf();
    }

    public boolean isGarbageInFrontOfSpaceship() {
        return spaceShip.isGarbageInFrontOf();
    }

    public boolean isWallInFrontOfSpaceship() {
        return spaceShip.isWallInFrontOf();
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

    public boolean hasSpaceShip() {
        return spaceShip != null;
    }

    public void setSpaceShip(SpaceShip spaceShip) {
        this.spaceShip = spaceShip;
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
        var commandHandler = commandHandlerFactory.create(command);
        commandHandler.handle(this);
    }

    private void incrementSteps() {
        steps++;
        if (steps >= 1500 || score < 0) {
            died();
        }
    }
}
