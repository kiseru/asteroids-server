package ru.kiseru.server.room;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import ru.kiseru.logics.Game;
import ru.kiseru.logics.Screen;
import ru.kiseru.logics.auxiliary.ScreenSize;
import ru.kiseru.logics.models.Spaceship;
import ru.kiseru.server.Server;
import ru.kiseru.server.UserHandler;

public final class Room {

    private static final int MAX_USERS = 1;

    private final List<UserHandler> userHandlers = new ArrayList<>();

    private volatile Status status = Status.WAITING_CONNECTIONS;
    private volatile Game game;

    public synchronized Room addUser(UserHandler userHandler) {
        if (isFull()) return Server.Companion.getNotFullRoom().addUser(userHandler);

        for (var handler : userHandlers) {
            handler.sendMessage(String.format("User %s has joined the room.", userHandler.getUser().username()));
        }

        userHandlers.add(userHandler);

        return this;
    }

    public synchronized void removeUser(UserHandler userHandler) {
        if (userHandlers.isEmpty()) {
            return;
        }

        userHandlers.remove(userHandler);
    }

    public String getRating() {
        return userHandlers.stream()
                .sorted(Comparator.comparingInt(UserHandler::getScore))
                .map(UserHandler::toString)
                .collect(Collectors.joining("\n"));
    }

    public long aliveCount() {
        return userHandlers.stream()
                .map(UserHandler::getSpaceship)
                .filter(spaceship -> spaceship != null && spaceship.getStatus() == Spaceship.Status.ALIVE)
                .count();
    }

    public boolean isFull() {
        return userHandlers.size() >= MAX_USERS;
    }

    public boolean isGameStarted() {
        return status == Status.GAMING;
    }

    public synchronized void awaitStart() throws InterruptedException {
        while (!isGameStarted() && !isGameFinished()) {
            wait();
        }
    }

    public boolean isGameFinished() {
        return status == Status.FINISHED;
    }

    public synchronized void notifyIfNoAliveUsers() {
        if (aliveCount() == 0) {
            notifyAll();
        }
    }

    public void run() {
        synchronized (this) {
            status = Status.GAMING;
            game = new Game(new Screen(new ScreenSize(30, 30)), 150, 50);
            userHandlers.forEach(userHandler -> game.registerSpaceshipForUser(userHandler, userHandler.getUser().id()));
            userHandlers.forEach(userHandler -> userHandler.sendMessage("start"));
            notifyAll();
        }

        try {
            game.refresh();
            synchronized (this) {
                while (aliveCount() > 0) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        synchronized (this) {
            status = Status.FINISHED;
        }

        userHandlers.forEach(userHandler -> userHandler.sendMessage("finish"));
        userHandlers.forEach(userHandler -> userHandler.sendMessage(this.getRating()));
        System.out.println("Room released!");
        System.out.println(getRating());
        System.out.println();
    }

    public Game getGame() {
        return game;
    }

    private enum Status {
        WAITING_CONNECTIONS,
        GAMING,
        FINISHED
    }
}
