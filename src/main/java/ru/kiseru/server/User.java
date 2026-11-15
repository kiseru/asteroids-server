package ru.kiseru.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;
import ru.kiseru.logics.auxiliary.Direction;
import ru.kiseru.logics.models.Spaceship;
import ru.kiseru.server.room.Room;

public final class User {

    private final long id;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private volatile Room room;

    private String username;
    private final AtomicInteger score = new AtomicInteger(100);
    private int steps = 0;
    private volatile boolean isAlive = true;
    private Spaceship spaceship;

    public User(
        long id,
        BufferedReader reader,
        PrintWriter writer,
        Room room
    ) {
        this.id = id;
        this.reader = reader;
        this.writer = writer;
        this.room = room;
    }

    public int getScore() {
        return score.get();
    }

    public void run() {
        try {
            writer.println("Welcome To Asteroids Server");
            writer.println("Please, introduce yourself!");
            username = reader.readLine();
            System.out.println(username + " has joined the server!");
            writer.println("You need to keep a space garbage.");
            writer.println("Your ID is " + getId());
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
            while (!room.isGameFinished() && isAlive) {
                String userMessage = reader.readLine();
                switch (userMessage) {
                    case "go" -> {
                        spaceship.go();
                        room.getGame().refresh();
                        sendMessage(Integer.toString(score.get()));
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
                steps++;
                if (steps >= 1500) {
                    died();
                }
                if (score.get() < 0) {
                    died();
                }
            }
        } catch (IOException e) {
            System.out.println("Connection problems with user " + username);
        } finally {
            isAlive = false;
            room.notifyIfNoAliveUsers();
        }
    }

    @Override
    public String toString() {
        return String.format("%s %d", username, score.get());
    }

    public String getUsername() {
        return username;
    }

    public long getId() {
        return id;
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void addScore() {
        if (room.isGameStarted()) {
            score.addAndGet(10);
        }
    }

    public void subtractScore() {
        if (room.isGameStarted() && score.addAndGet(-50) < 0) {
            isAlive = false;
        }
    }

    public boolean getIsAlive() {
        return isAlive;
    }

    public void died() {
        isAlive = false;
        this.sendMessage("died");
        String scoreMessage = String.format("You have collected %d score", this.score.get());
        this.sendMessage(scoreMessage);
    }

    public void setSpaceship(Spaceship spaceship) {
        this.spaceship = spaceship;
    }

    public Room getRoom() {
        return room;
    }
}
