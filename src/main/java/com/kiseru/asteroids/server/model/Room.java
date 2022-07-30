package com.kiseru.asteroids.server.model;

import com.kiseru.asteroids.server.Server;
import com.kiseru.asteroids.server.User;
import com.kiseru.asteroids.server.logics.Game;
import com.kiseru.asteroids.server.logics.Screen;
import com.kiseru.asteroids.server.room.RoomStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public final class Room extends Thread {

    private static final int MAX_USERS = 1;

    private final ArrayList<User> users = new ArrayList<>();

    private RoomStatus roomStatus = RoomStatus.WAITING_CONNECTIONS;

    private Game game;

    public synchronized void addUser(User user) {
        if (users.size() >= MAX_USERS) {
            Server.Companion.getNotFullRoom().addUser(user);
        }

        users.stream()
                .filter(Objects::nonNull)
                .forEach(roomUser -> roomUser.sendMessage(String.format("User %s has joined the room.", user.getUserName())));

        users.add(user);
    }

    public synchronized void removeUser(User user) {
        if (users.isEmpty()) {
            return;
        }

        users.remove(user);
    }

    public String getRating() {
        return users.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(User::getScore))
                .map(User::toString)
                .reduce("", (acc, userRow) -> acc + userRow + "\n");
    }

    public long aliveUsersCount() {
        return users.stream()
                .filter(Objects::nonNull)
                .filter(User::getIsAlive)
                .count();
    }

    public boolean isFull() {
        return users.size() >= MAX_USERS;
    }

    public boolean isGameStarted() {
        return roomStatus == RoomStatus.GAMING;
    }

    public boolean isGameFinished() {
        return roomStatus == RoomStatus.FINISHED;
    }

    @Override
    public void run() {
        users.stream()
                .filter(Objects::nonNull)
                .forEach(user -> user.sendMessage("start"));

        roomStatus = RoomStatus.GAMING;

        synchronized (Server.class) {
            game = new Game(new Screen(30, 30), 150, 50);
            users.stream()
                    .filter(Objects::nonNull)
                    .forEach(game::registerSpaceShipForUser);
            Server.class.notifyAll();
        }

        synchronized (this) {
            try {
                game.refresh();
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                interrupt();
            }
            roomStatus = RoomStatus.FINISHED;
        }

        users.stream()
                .filter(Objects::nonNull)
                .forEach(user -> user.sendMessage("finish"));
        users.stream()
                .filter(Objects::nonNull)
                .forEach(user -> user.sendMessage(this.getRating()));
        System.out.println("Room released!");
        System.out.println(getRating());
        System.out.println();
    }

    public Game getGame() {
        return game;
    }
}
