package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.logics.auxiliary.Direction;
import com.kiseru.asteroids.server.logics.models.SpaceShip;
import com.kiseru.asteroids.server.room.Room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class User implements Runnable {

    private final BufferedReader reader;
    private final OutputStream outputStream;
    private final PrintWriter writer;
    private final Room room;
    private final Supplier<Room> notFullRoomSupplier;
    private final BiConsumer<Room, OutputStream> onWriteGameField;
    private final int id = new Random().nextInt(100);

    private String userName;
    private int score = 100;
    private int steps = 0;
    private boolean isAlive = true;
    private SpaceShip spaceShip;

    public User(
            Socket newConnection,
            Room room,
            Supplier<Room> notFullRoomSupplier,
            BiConsumer<Room, OutputStream> onWriteGameField
    ) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(newConnection.getInputStream()));
        this.outputStream = newConnection.getOutputStream();
        this.writer = new PrintWriter(outputStream, true);
        this.room = room;
        this.notFullRoomSupplier = notFullRoomSupplier;
        this.onWriteGameField = onWriteGameField;
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
            writer.println("Your ID is " + id);
            writer.println("Good luck, Commander!");
            synchronized (room) {
                try {
                    room.addUser(this);
                    if (room.isFull()) {
                        notFullRoomSupplier.get();// Чтобы полная комната добавилась в список комнат
                        new Thread(room).start();
                    }
                    room.wait();
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
                } else if (userMessage.equals("GAME_FIELD")) {
                    handleGameFieldCommand();
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
            if (room.aliveCount() == 0) {
                synchronized (room) {
                    room.notify();
                }
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s %d", userName, score);
    }

    public String getUserName() {
        return userName;
    }

    private void handleGameFieldCommand() {
        onWriteGameField.accept(room, outputStream);
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

    public int getId() {
        return id;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
