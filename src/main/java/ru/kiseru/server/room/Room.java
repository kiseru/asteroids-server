package ru.kiseru.server.room;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.IntStream;
import ru.kiseru.logics.Game;
import ru.kiseru.logics.Screen;
import ru.kiseru.server.Server;
import ru.kiseru.server.User;

public final class Room {

    private final int MAX_USERS = 1;

    private final ArrayList<User> users = new ArrayList<>();

    private volatile int usersCount = 0;
    private volatile Status status = Status.WAITING_CONNECTIONS;
    private Game game;

    public Room() {
        initUsers();
    }

    private void initUsers() {
        for (int i = 0; i < MAX_USERS; i++) {
            users.add(null);
        }
    }

    public synchronized Room addUser(User user) {
        if (isFull()) return Server.Companion.getNotFullRoom().addUser(user);

        int emptyPlaceIndex = IntStream.iterate(0, index -> index + 1)
                .limit(users.size())
                .filter(index -> users.get(index) == null)
                .findFirst()
                .orElse(-1);

        if (emptyPlaceIndex == -1) return this;

        users.stream()
                .filter(Objects::nonNull)
                .forEach(roomUser -> roomUser.sendMessage(String.format("User %s has joined the room.", user.getUsername())));

        users.set(emptyPlaceIndex, user);
        usersCount++;

        return this;
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
            game = new Game(new Screen(30, 30), 150, 50);
            users.stream()
                    .filter(Objects::nonNull)
                    .forEach(game::registerSpaceShipForUser);
            users.stream()
                    .filter(Objects::nonNull)
                    .forEach(user -> user.sendMessage("start"));
            notifyAll();

            try {
                game.refresh();
                while (aliveCount() > 0) {
                    wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            status = Status.FINISHED;
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

    private enum Status {
        WAITING_CONNECTIONS,
        GAMING,
        FINISHED
    }
}
