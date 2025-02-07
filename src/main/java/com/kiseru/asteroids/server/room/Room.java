package com.kiseru.asteroids.server.room;

import com.kiseru.asteroids.server.logics.Game;
import com.kiseru.asteroids.server.logics.Screen;
import com.kiseru.asteroids.server.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class Room {

    private final int MAX_USERS = 1;
    private final ArrayList<User> users = new ArrayList<>();
    private final List<Consumer<String>> onMessageSendHandlers = new ArrayList<>();

    private final Supplier<Room> notFullRoomSupplier;
    private final Consumer<Room> onRoomRun;

    private int usersCount = 0;
    private RoomStatus status = RoomStatus.WAITING_CONNECTIONS;
    private Game game;

    public Room(Supplier<Room> notFullRoomSupplier, Consumer<Room> onRoomRun) {
        this.notFullRoomSupplier = notFullRoomSupplier;
        this.onRoomRun = onRoomRun;
        IntStream.iterate(0, i -> i + 1)
                .limit(MAX_USERS)
                .forEach(i -> users.add(null));
    }

    public void addUser(User user, Consumer<String> onMessageSend) {
        if (usersCount >= MAX_USERS) {
            var notFullRoom = notFullRoomSupplier.get();
            notFullRoom.addUser(user, onMessageSend);
        }

        int emptyPlaceIndex = IntStream.iterate(0, index -> index + 1)
                .limit(users.size())
                .filter(index -> users.get(index) == null)
                .findFirst()
                .orElse(-1);

        if (emptyPlaceIndex == -1) return;

        var message = String.format("User %s has joined the room.", user.getUsername());
        for (var handler : onMessageSendHandlers) {
            handler.accept(message);
        }

        users.set(emptyPlaceIndex, user);
        onMessageSendHandlers.add(onMessageSend);
        usersCount++;
    }

    public String getRating() {
        return users.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(User::getScore))
                .map(User::toString)
                .reduce("", (acc, userRow) -> acc + userRow + "\n");
    }

    public long aliveCount() {
        return users.stream()
                .filter(Objects::nonNull)
                .filter(User::isAlive)
                .count();
    }

    public boolean isFull() {
        return usersCount == MAX_USERS;
    }

    public void createGame(Lock lock, Condition condition) {
        game = new Game(new Screen(30, 30), 150, 50);
        users.stream()
                .filter(Objects::nonNull)
                .forEach(user -> game.registerSpaceShipForUser(user, lock, condition, this));
    }

    public void waitStart(
            User user,
            Consumer<String> onMessageSend,
            Supplier<Room> notFullRoomSupplier,
            Lock lock,
            Condition condition
    ) {
        addUser(user, onMessageSend);
        if (isFull()) {
            notFullRoomSupplier.get();// Чтобы полная комната добавилась в список комнат
            for (var handler : onMessageSendHandlers) {
                handler.accept("start");
            }
            status = RoomStatus.GAMING;
            createGame(lock, condition);
            game.refresh();
            Runnable runnable = () -> onRoomRun.accept(this);
            new Thread(runnable).start();
        }
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

    public List<Consumer<String>> getOnMessageSendHandlers() {
        return Collections.unmodifiableList(onMessageSendHandlers);
    }
}
