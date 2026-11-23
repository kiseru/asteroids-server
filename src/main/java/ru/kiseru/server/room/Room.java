package ru.kiseru.server.room;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.IntStream;
import ru.kiseru.logics.Game;
import ru.kiseru.logics.Screen;
import ru.kiseru.logics.auxiliary.ScreenSize;
import ru.kiseru.logics.models.Spaceship;
import ru.kiseru.server.Server;
import ru.kiseru.server.UserHandler;

public final class Room {

    private final int MAX_USERS = 1;

    private final ArrayList<UserHandler> userHandlers = new ArrayList<>();

    private volatile int userHandlersCount = 0;
    private volatile Status status = Status.WAITING_CONNECTIONS;
    private volatile Game game;

    public Room() {
        initUserHandlers();
    }

    private void initUserHandlers() {
        for (int i = 0; i < MAX_USERS; i++) {
            userHandlers.add(null);
        }
    }

    public synchronized Room addUser(UserHandler userHandler) {
        if (isFull()) return Server.Companion.getNotFullRoom().addUser(userHandler);

        int emptyPlaceIndex = IntStream.iterate(0, index -> index + 1)
                .limit(userHandlers.size())
                .filter(index -> userHandlers.get(index) == null)
                .findFirst()
                .orElse(-1);

        if (emptyPlaceIndex == -1) return this;

        userHandlers.stream()
                .filter(Objects::nonNull)
                .forEach(roomUserHandler -> roomUserHandler.sendMessage(String.format("User %s has joined the room.", userHandler.getUser().username())));

        userHandlers.set(emptyPlaceIndex, userHandler);
        userHandlersCount++;

        return this;
    }

    public synchronized void removeUser(UserHandler userHandler) {
        if (userHandlersCount == 0) return;

        UserHandler removingUserHandler = userHandlers.stream()
                .filter(userHandler::equals)
                .findFirst()
                .orElse(null);

        if (removingUserHandler == null) return;

        userHandlers.remove(removingUserHandler);

        userHandlersCount--;
    }

    public String getRating() {
        return userHandlers.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(UserHandler::getScore))
                .map(UserHandler::toString)
                .reduce("", (acc, userHandlerRow) -> acc + userHandlerRow + "\n");
    }

    public long aliveCount() {
        return userHandlers.stream()
                .filter(Objects::nonNull)
                .map(UserHandler::getSpaceship)
                .filter(Objects::nonNull)
                .filter(spaceship -> spaceship.getStatus() == Spaceship.Status.ALIVE)
                .count();
    }

    public boolean isFull() {
        return userHandlersCount == MAX_USERS;
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
            userHandlers.stream()
                    .filter(Objects::nonNull)
                    .forEach(game::registerSpaceShipForUser);
            userHandlers.stream()
                    .filter(Objects::nonNull)
                    .forEach(userHandler -> userHandler.sendMessage("start"));
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

        userHandlers.stream()
                .filter(Objects::nonNull)
                .forEach(userHandler -> userHandler.sendMessage("finish"));
        userHandlers.stream()
                .filter(Objects::nonNull)
                .forEach(userHandler -> userHandler.sendMessage(this.getRating()));
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
