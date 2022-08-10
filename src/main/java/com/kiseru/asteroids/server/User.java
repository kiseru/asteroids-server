package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.exception.GameFinishedException;
import com.kiseru.asteroids.server.handler.CommandHandlerFactory;
import com.kiseru.asteroids.server.handler.impl.CommandHandlerFactoryImpl;
import com.kiseru.asteroids.server.model.Direction;
import com.kiseru.asteroids.server.model.Room;
import com.kiseru.asteroids.server.model.Spaceship;
import com.kiseru.asteroids.server.service.MessageReceiverService;
import com.kiseru.asteroids.server.service.MessageSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public final class User implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(User.class);

    private static final CommandHandlerFactory commandHandlerFactory = CommandHandlerFactoryImpl.INSTANCE;

    private static int nextId = 0;

    private final Room room;

    private final int id;

    private final String username;

    private final Socket socket;

    private final MessageReceiverService messageReceiverService;

    private final MessageSenderService messageSenderService;

    private int score = 100;

    private int steps = 0;

    private boolean isAlive = true;

    private Spaceship spaceship;

    public User(String username,
                Room room,
                Socket socket,
                MessageReceiverService messageReceiverService,
                MessageSenderService messageSenderService) {
        this.username = username;
        this.room = room;
        this.socket = socket;
        this.messageReceiverService = messageReceiverService;
        this.messageSenderService = messageSenderService;
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
                String command = messageReceiverService.receive();
                handleCommand(command);
                incrementSteps();
            }
        } finally {
            isAlive = false;
            room.setGameFinished();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        messageSenderService.send(message);
    }

    public void addScore() {
        if (room.isGameStarted()) {
            score += 10;
        }
    }

    public void subtractScore() {
        if (!room.isGameStarted()) {
            throw new GameFinishedException();
        }

        score -= 50;
        if (score < 0) {
            isAlive = false;
        }
    }

    public void died() {
        isAlive = false;
        messageSenderService.sendGameOver(score);
    }

    public void checkCollectedGarbage(int collected) {
        room.checkCollectedGarbage(collected);
    }

    public void moveSpaceship() {
        spaceship.go();
    }

    public void refreshRoom() {
        room.refresh();
    }

    public void sendScore() {
        messageSenderService.sendScore(score);
    }

    public void setSpaceshipDirection(Direction direction) {
        spaceship.setDirection(direction);
    }

    public boolean isAsteroidInFrontOfSpaceship() {
        return spaceship.isAsteroidInFrontOf();
    }

    public boolean isGarbageInFrontOfSpaceship() {
        return spaceship.isGarbageInFrontOf();
    }

    public boolean isWallInFrontOfSpaceship() {
        return spaceship.isWallInFrontOf();
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

    public boolean hasSpaceship() {
        return spaceship != null;
    }

    public void setSpaceship(Spaceship spaceship) {
        this.spaceship = spaceship;
    }

    public void closeConnection() {
        try {
            log.info("Closing connection with {}", username);
            messageSenderService.sendExit();
            socket.close();
            log.info("Connection with {} has been closed", username);
        } catch (IOException e) {
            log.error("Failed to close connection", e);
        }
    }

    public void sendSuccessMessage() {
        messageSenderService.sendSuccess();
    }

    public void sendUnknownCommandMessage() {
        messageSenderService.sendUnknownCommand();
    }

    private void init() {
        room.addUserToRoom(this);
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
