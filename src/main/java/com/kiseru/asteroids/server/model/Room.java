package com.kiseru.asteroids.server.model;

import com.kiseru.asteroids.server.Server;
import com.kiseru.asteroids.server.User;
import com.kiseru.asteroids.server.logics.Game;
import com.kiseru.asteroids.server.logics.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Room implements Runnable {

    private static final int SCREEN_WIDTH = 30;

    private static final int SCREEN_HEIGHT = 30;

    private static final int NUMBER_OF_GARBAGE_CELLS = 150;

    private static final int NUMBER_OF_ASTEROID_CELLS = 50;

    private static final int MAX_USERS = 1;

    private static final Logger log = LoggerFactory.getLogger(Room.class);

    private final Lock lock = new ReentrantLock();

    private final Condition endgameCondition = lock.newCondition();

    private final List<User> users = new CopyOnWriteArrayList<>();

    private Status status = Status.WAITING_CONNECTIONS;

    private Game game;

    public void addUser(User user) {
        if (users.size() >= MAX_USERS) {
            Server.Companion.getNotFullRoom().addUser(user);
        }

        users.stream()
                .filter(Objects::nonNull)
                .forEach(roomUser -> roomUser.sendMessage(String.format("User %s has joined the room.", user.getUserName())));

        users.add(user);
    }

    public void removeUser(User user) {
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
        return status == Status.GAMING;
    }

    public boolean isGameFinished() {
        return status == Status.FINISHED;
    }

    @Override
    public void run() {
        users.stream()
                .filter(Objects::nonNull)
                .forEach(user -> user.sendMessage("start"));

        status = Status.GAMING;

        synchronized (Server.class) {
            game = new Game(new Screen(SCREEN_WIDTH, SCREEN_HEIGHT), NUMBER_OF_GARBAGE_CELLS, NUMBER_OF_ASTEROID_CELLS);
            users.stream()
                    .filter(Objects::nonNull)
                    .forEach(game::registerSpaceShipForUser);
            Server.class.notifyAll();
        }

        game.refresh();
        try {
            lock.lock();
            while (status != Status.FINISHED) {
                endgameCondition.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }

        handleGameFinished();
        log.info("Room released! Rating table:\n{}", getRating());
    }

    public Game getGame() {
        return game;
    }

    public void setGameFinished() {
        lock.lock();
        try {
            log.info("Game finished");
            status = Status.FINISHED;
            endgameCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void handleGameFinished() {
        for (User user : users) {
            if (user != null) {
                user.sendMessage("finish\n" + this.getRating());
            }
        }
    }

    private enum Status {
        WAITING_CONNECTIONS,
        GAMING,
        FINISHED
    }
}
