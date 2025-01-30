package com.kiseru.asteroids.server.room;

import com.kiseru.asteroids.server.logics.Game;
import com.kiseru.asteroids.server.logics.Screen;
import com.kiseru.asteroids.server.User;

import com.kiseru.asteroids.server.service.RoomService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.IntStream;

public class Room implements Runnable {

    private final int MAX_USERS = 1;
    private final ArrayList<User> users = new ArrayList<>();

    private final RoomService roomService;

    private int usersCount = 0;
    private RoomStatus roomStatus = RoomStatus.WAITING_CONNECTIONS;
    private Game game;

    public Room(RoomService roomService) {
        this.roomService = roomService;
        IntStream.iterate(0, i -> i + 1)
                .limit(MAX_USERS)
                .forEach(i -> users.add(null));
    }

    public synchronized void addUser(User user) {
        if (usersCount >= MAX_USERS) {
            var notFullRoom = roomService.getNotFullRoom();
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
                .forEach(roomUser -> roomUser.sendMessage(String.format("User %s has joined the room.", user.getUserName())));

        users.set(emptyPlaceIndex, user);
        usersCount++;
    }

    public synchronized void removeUser(User user) {
        if (usersCount == 0) return;

        User removingUser = users.stream()
                .filter(user::equals)
                .findFirst()
                .orElse(null);

        if (removingUser == null) return;

        users.remove(removingUser);

        usersCount--;
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
                .filter(User::getIsAlive)
                .count();
    }

    public boolean isFull() {
        return usersCount == MAX_USERS;
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

        synchronized (this) {
            game = new Game(new Screen(30, 30), 150, 50);
            users.stream()
                    .filter(Objects::nonNull)
                    .forEach(game::registerSpaceShipForUser);
            notifyAll();
        }

        synchronized (this) {
            try {
                game.refresh();
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
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
