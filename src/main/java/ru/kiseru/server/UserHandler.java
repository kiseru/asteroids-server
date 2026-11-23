package ru.kiseru.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import ru.kiseru.logics.Steps;
import ru.kiseru.logics.User;
import ru.kiseru.logics.auxiliary.Direction;
import ru.kiseru.logics.models.Spaceship;
import ru.kiseru.server.room.Room;

public final class UserHandler {

    private final BufferedReader reader;
    private final PrintWriter writer;
    private volatile Room room;

    private final User user;
    private Spaceship spaceship;

    public UserHandler(
        BufferedReader reader,
        PrintWriter writer,
        Room room,
        User user
    ) {
        this.reader = reader;
        this.writer = writer;
        this.room = room;
        this.user = user;
    }

    public int getScore() {
        if (spaceship == null) {
            return 0;
        }
        return spaceship.getScore().value();
    }

    public void run() {
        try {
            System.out.println(user.username() + " has joined the server!");
            writer.println("You need to keep a space garbage.");
            writer.println("Your ID is " + getUser().id());
            writer.println("Good luck, Commander!");
            room = room.addUser(this);
            if (room.isFull()) {
                Server.Companion.getNotFullRoom();
                new Thread(room::run).start();
            }
            try {
                room.awaitStart();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            spaceship.setDirection(Direction.UP);
            while (!room.isGameFinished() && spaceship.isAlive()) {
                String userMessage = reader.readLine();
                switch (userMessage) {
                    case "go" -> {
                        spaceship.go();
                        room.getGame().refresh();
                        sendMessage(Integer.toString(getScore()));
                    }
                    case "left" -> {
                        spaceship.setDirection(Direction.LEFT);
                        room.getGame().refresh();
                        sendMessage("success");
                    }
                    case "right" -> {
                        spaceship.setDirection(Direction.RIGHT);
                        room.getGame().refresh();
                        sendMessage("success");
                    }
                    case "up" -> {
                        spaceship.setDirection(Direction.UP);
                        room.getGame().refresh();
                        sendMessage("success");
                    }
                    case "down" -> {
                        spaceship.setDirection(Direction.DOWN);
                        room.getGame().refresh();
                        sendMessage("success");
                    }
                    case "isAsteroid" -> sendMessage(spaceship.getCourseChecker().isAsteroid() ? "t" : "f");
                    case "isGarbage" -> sendMessage(spaceship.getCourseChecker().isGarbage() ? "t" : "f");
                    case "isWall" -> sendMessage(spaceship.getCourseChecker().isWall() ? "t" : "f");
                    default -> sendMessage("Unknown command");
                }
                Steps nextSteps = spaceship.getSteps().increment();
                spaceship.setSteps(nextSteps);
                if (nextSteps.getValue() >= 1500 || getScore() < 0) {
                    died();
                }
            }
        } catch (IOException e) {
            System.out.println("Connection problems with user " + user.username());
        } finally {
            synchronized (spaceship) {
                if (spaceship != null && spaceship.isAlive()) {
                    spaceship.destroy();
                }
            }
            room.notifyIfNoAliveUsers();
        }
    }

    @Override
    public String toString() {
        return String.format("%s %d", user.username(), getScore());
    }

    public User getUser() {
        return user;
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void died() {
        synchronized (spaceship) {
            if (spaceship.isAlive()) {
                spaceship.destroy();
            }
        }
        this.sendMessage("died");
        String scoreMessage = String.format("You have collected %d score", getScore());
        this.sendMessage(scoreMessage);
    }

    public void setSpaceship(Spaceship spaceship) {
        this.spaceship = spaceship;
    }

    public Spaceship getSpaceship() {
        return spaceship;
    }

    public Room getRoom() {
        return room;
    }
}
