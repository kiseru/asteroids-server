package com.kiseru.asteroids.server.room;

import com.kiseru.asteroids.server.logics.Game;
import com.kiseru.asteroids.server.User;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Room {

    private final User user;
    private final Consumer<String> onMessageSendHandler;

    private final UUID id;
    private final String name;

    private RoomStatus status = RoomStatus.WAITING_CONNECTIONS;
    private Game game;

    public Room(UUID id, String name, User user, Consumer<String> onMessageSendHandler) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.onMessageSendHandler = onMessageSendHandler;
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
        return Collections.singletonList(user);
    }

    public List<Consumer<String>> getOnMessageSendHandlers() {
        return Collections.singletonList(onMessageSendHandler);
    }

    public String getName() {
        return name;
    }
}
