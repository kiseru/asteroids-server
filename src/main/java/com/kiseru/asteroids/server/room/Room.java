package com.kiseru.asteroids.server.room;

import com.kiseru.asteroids.server.logics.Game;
import com.kiseru.asteroids.server.logics.Screen;
import com.kiseru.asteroids.server.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class Room {

    private final int MAX_USERS = 1;
    private final ArrayList<User> users = new ArrayList<>();

    private final Supplier<Room> notFullRoomSupplier;
    private final BiConsumer<Room, List<User>> onRoomRun;

    private int usersCount = 0;
    private RoomStatus status = RoomStatus.WAITING_CONNECTIONS;
    private Game game;

    public Room(Supplier<Room> notFullRoomSupplier, BiConsumer<Room, List<User>> onRoomRun) {
        this.notFullRoomSupplier = notFullRoomSupplier;
        this.onRoomRun = onRoomRun;
        IntStream.iterate(0, i -> i + 1)
                .limit(MAX_USERS)
                .forEach(i -> users.add(null));
    }

    public synchronized void addUser(User user) {
        if (usersCount >= MAX_USERS) {
            var notFullRoom = notFullRoomSupplier.get();
            notFullRoom.addUser(user);
        }

        int emptyPlaceIndex = IntStream.iterate(0, index -> index + 1)
                .limit(users.size())
                .filter(index -> users.get(index) == null)
                .findFirst()
                .orElse(-1);

        if (emptyPlaceIndex == -1) return;

        users.stream()
                .filter(Objects::nonNull)
                .forEach(roomUser -> roomUser.sendMessage(String.format(
                        "User %s has joined the room.",
                        user.getUsername()
                )));

        users.set(emptyPlaceIndex, user);
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

    public boolean isGameStarted() {
        return status == RoomStatus.GAMING;
    }

    public boolean isGameFinished() {
        return status == RoomStatus.FINISHED;
    }

    public synchronized void createGame() {
        game = new Game(new Screen(30, 30), 150, 50);
        users.stream()
                .filter(Objects::nonNull)
                .forEach(game::registerSpaceShipForUser);
        notifyAll();
    }

    public synchronized void waitFinish() {
        try {
            game.refresh();
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        status = RoomStatus.FINISHED;
    }

    public synchronized void waitStart(User user, Supplier<Room> notFullRoomSupplier) {
        try {
            addUser(user);
            if (isFull()) {
                notFullRoomSupplier.get();// Чтобы полная комната добавилась в список комнат
                Runnable runnable = () -> onRoomRun.accept(this, users);
                new Thread(runnable).start();
            }
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void checkAlive() {
        if (aliveCount() == 0) {
            notifyAll();
        }
    }

    public synchronized void checkCollectedGarbage(Game game) {
        int collected = game.incrementCollectedGarbageCount();
        if (collected >= game.getGarbageNumber()) {
            notifyAll();
        }
    }

    public Game getGame() {
        return game;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}
