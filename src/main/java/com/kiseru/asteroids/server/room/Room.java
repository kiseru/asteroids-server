package com.kiseru.asteroids.server.room;

import com.kiseru.asteroids.server.logics.Game;
import com.kiseru.asteroids.server.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Room {

    private final List<User> users = new ArrayList<>();
    private final List<Consumer<String>> sendMessageHandlers = new ArrayList<>();

    private final UUID id;
    private final String name;
    private final Game game;
    private final int size;

    private RoomStatus status = RoomStatus.WAITING_CONNECTIONS;

    public Room(UUID id, String name, Game game, int size) {
        this.id = id;
        this.name = name;
        this.game = game;
        this.size = size;
    }

    public void addUser(User user, Consumer<String> onMessageSend) {
        users.add(user);
        sendMessageHandlers.add(onMessageSend);
    }

    public Game getGame() {
        return game;
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

    public List<Consumer<String>> getSendMessageHandlers() {
        return Collections.unmodifiableList(sendMessageHandlers);
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }
}
