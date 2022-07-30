package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.logics.auxiliary.Direction;
import com.kiseru.asteroids.server.logics.models.SpaceShip;
import com.kiseru.asteroids.server.model.Room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class User extends Thread {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private BufferedReader reader;
    private PrintWriter writer;
    private String userName;
    private int score;
    private int steps;
    private boolean isAlive;
    private final Room room;
    private SpaceShip spaceShip;

    User(Socket newConnection, Room room) throws IOException {
        reader = new BufferedReader(new InputStreamReader(newConnection.getInputStream()));
        writer = new PrintWriter(newConnection.getOutputStream(), true);
        score = 100;
        steps = 0;
        isAlive = true;
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
            userName = reader.readLine();
            System.out.println(userName + " has joined the server!");
            writer.println("You need to keep a space garbage.");
            writer.println("Your ID is " + getId());
            writer.println("Good luck, Commander!");
            synchronized (Server.class) {
                try {
                    room.addUser(this);
                    if (room.isFull()) {
                        Server.Companion.getNotFullRoom(); // Чтобы полная комната добавилась в список комнат
                        EXECUTOR_SERVICE.execute(room);
                    }
                    Server.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            spaceShip.setDirection(Direction.UP);
            while (!room.isGameFinished() && isAlive) {
                String userMessage = reader.readLine();
                if (userMessage.equals("go")) {
                    spaceShip.go();
                    room.getGame().refresh();
                    sendMessage(Integer.toString(score));
                } else if (userMessage.equals("left")) {
                    spaceShip.setDirection(Direction.LEFT);
                    room.getGame().refresh();
                    sendMessage("success");
                } else if (userMessage.equals("right")) {
                    spaceShip.setDirection(Direction.RIGHT);
                    room.getGame().refresh();
                    sendMessage("success");
                } else if (userMessage.equals("up")) {
                    spaceShip.setDirection(Direction.UP);
                    room.getGame().refresh();
                    sendMessage("success");
                } else if (userMessage.equals("down")) {
                    spaceShip.setDirection(Direction.DOWN);
                    room.getGame().refresh();
                    sendMessage("success");
                } else if (userMessage.equals("isAsteroid")) {
                    sendMessage(spaceShip.getCourseChecker().isAsteroid() ? "t" : "f");
                } else if (userMessage.equals("isGarbage")) {
                    sendMessage(spaceShip.getCourseChecker().isGarbage() ? "t" : "f");
                } else if (userMessage.equals("isWall")) {
                    sendMessage(spaceShip.getCourseChecker().isWall() ? "t" : "f");
                } else {
                    sendMessage("Unknown command");
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
            System.out.println("Connection problems with user " + userName);
        } finally {
            isAlive = false;
            room.setGameFinished();
        }
    }

    @Override
    public String toString() {
        return String.format("%s %d", userName, score);
    }

    public String getUserName() {
        return userName;
    }

    public void sendMessage(String message) {
        writer.println(message);
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

    public boolean getIsAlive() {
        return isAlive;
    }

    public void died() {
        isAlive = false;
        this.sendMessage("died");
        String scoreMessage = String.format("You have collected %d score", this.score);
        this.sendMessage(scoreMessage);
    }

    public void setSpaceShip(SpaceShip spaceShip) {
        this.spaceShip = spaceShip;
    }

    public Room getRoom() {
        return room;
    }
}
