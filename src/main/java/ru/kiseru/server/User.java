package ru.kiseru.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import ru.kiseru.logics.auxiliary.Direction;
import ru.kiseru.logics.models.Spaceship;
import ru.kiseru.server.room.Room;

final public class User extends Thread {

    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Room room;

    private String username;
    private int score = 100;
    private int steps = 0;
    private boolean isAlive = true;
    private Spaceship spaceship;

    public User(
        BufferedReader reader,
        PrintWriter writer,
        Room room
    ) {
        this.reader = reader;
        this.writer = writer;
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
            username = reader.readLine();
            System.out.println(username + " has joined the server!");
            writer.println("You need to keep a space garbage.");
            writer.println("Your ID is " + getId());
            writer.println("Good luck, Commander!");
            synchronized (Server.class) {
                try {
                    room.addUser(this);
                    if (room.isFull()) {
                        Server.getNotFullRoom(); // Чтобы полная комната добавилась в список комнат
                        room.start();
                    }
                    Server.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            spaceship.setDirection(Direction.UP);
            while (!room.isGameFinished() && isAlive) {
                String userMessage = reader.readLine();
                switch (userMessage) {
                    case "go" -> {
                        spaceship.go();
                        room.getGame().refresh();
                        sendMessage(Integer.toString(score));
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
                if (score < 0) {
                    died();
                }
            }
        } catch (IOException e) {
            System.out.println("Connection problems with user " + username);
        } finally {
            isAlive = false;
            if (room.aliveCount() == 0) {
                synchronized (room) {
                    room.notify();
                }
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s %d", username, score);
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void addScore() {
        if (room.isGameStarted()) score += 10;
    }

    public void subtractScore() {
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
        this.sendMessage("died");
        String scoreMessage = String.format("You have collected %d score", this.score);
        this.sendMessage(scoreMessage);
    }

    public void setSpaceship(Spaceship spaceship) {
        this.spaceship = spaceship;
    }

    public Room getRoom() {
        return room;
    }
}
