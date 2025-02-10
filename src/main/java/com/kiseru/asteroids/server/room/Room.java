package com.kiseru.asteroids.server.room;

import com.kiseru.asteroids.server.logics.Game;
import com.kiseru.asteroids.server.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Room {

    private final int MAX_USERS = 1;
    private final List<User> users = new ArrayList<>();
    private final List<Consumer<String>> onMessageSendHandlers = new ArrayList<>();

    private final UUID id;
    private final String name;
    private final Consumer<Room> onRoomRun;

    private int usersCount = 0;
    private RoomStatus status = RoomStatus.WAITING_CONNECTIONS;
    private Game game;

    public Room(UUID id, String name, Consumer<Room> onRoomRun) {
        this.id = id;
        this.name = name;
        this.onRoomRun = onRoomRun;
    }

    public void addUser(User user, Consumer<String> onMessageSend) {
        if (usersCount >= MAX_USERS) {
            return;
        }

        var message = String.format("User %s has joined the room.", user.getUsername());
        for (var handler : onMessageSendHandlers) {
            handler.accept(message);
        }

        users.add(user);
        onMessageSendHandlers.add(onMessageSend);
        usersCount++;
    }

    public boolean isFull() {
        return usersCount == MAX_USERS;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    public List<Consumer<String>> getOnMessageSendHandlers() {
        return Collections.unmodifiableList(onMessageSendHandlers);
    }

    public Consumer<Room> getOnRoomRun() {
        return onRoomRun;
    }

    public String getName() {
        return name;
    }
}
