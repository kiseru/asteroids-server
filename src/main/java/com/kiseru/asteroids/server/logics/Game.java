package com.kiseru.asteroids.server.logics;

import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.logics.auxiliary.Type;
import com.kiseru.asteroids.server.logics.models.Asteroid;
import com.kiseru.asteroids.server.logics.models.Crashable;
import com.kiseru.asteroids.server.logics.models.Garbage;
import com.kiseru.asteroids.server.logics.models.Point;
import com.kiseru.asteroids.server.logics.models.Spaceship;
import com.kiseru.asteroids.server.User;

import com.kiseru.asteroids.server.room.RoomStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Game {
    private final List<Point> pointsOnScreen;
    private final Screen screen;
    private final List<Runnable> crashHandlers;
    private final AtomicInteger collectedGarbageCount = new AtomicInteger(0);
    private final int garbageNumber;

    /**
     * Конструктор создания игровой сессии
     * @param screen - экран
     * @param garbageNumber - количество мусора для первоначальной генерации
     * @param asteroidNumber - количество астероидов для первоначальной генерации
     */
    public Game(Screen screen, int garbageNumber, int asteroidNumber) {
        this.garbageNumber = garbageNumber;
        this.screen = screen;
        this.pointsOnScreen = new ArrayList<>();
        this.crashHandlers = new ArrayList<>();

        // генерируем мусор
        for (int i = 0; i < garbageNumber; i++) {
            Garbage garbage = new Garbage(generateUniqueRandomCoordinates());
            pointsOnScreen.add(garbage);
        }
        // генерируем астероиды
        for (int i = 0; i < asteroidNumber; i++) {
            Asteroid asteroid = new Asteroid(generateUniqueRandomCoordinates());
            pointsOnScreen.add(asteroid);
        }
    }

    public void registerSpaceshipForUser(
            User user,
            Lock lock,
            Condition condition,
            Consumer<Spaceship> onSpaceshipCrash
    ) {
        Spaceship spaceship = new Spaceship(generateUniqueRandomCoordinates(), user, lock, condition);
        user.setSpaceship(spaceship);
        pointsOnScreen.add(spaceship);
        crashHandlers.add(() -> onSpaceshipCrash.accept(spaceship));
        spaceship.setCourseChecker(new CourseChecker(spaceship, this.pointsOnScreen, this.screen));
    }

    public void check(Game game, Spaceship spaceship, RoomStatus roomStatus, Consumer<RoomStatus> onRoomStatusUpdate) {
        List<Point> points = game.getPointsOnScreen();
        Point collisionPoint = null;
        for (Point point: points) {
            if (point.getType() != Type.SPACESHIP &&
                    point.isVisible() &&
                    point.getCoordinates().equals(spaceship.getCoordinates())) {
                collisionPoint = point;
                break;
            }
        }

        if (collisionPoint != null) {
            spaceship.crash(game, collisionPoint.getType(), roomStatus, onRoomStatusUpdate);
            ((Crashable)collisionPoint).crash();
        } else {
            // проверка на столкновение со стеной
            if (spaceship.getX() == 0 || spaceship.getY() == 0 ||
                    spaceship.getX() > game.getScreen().getWidth() ||
                    spaceship.getY() > game.getScreen().getHeight()) {
                spaceship.crash(game, Type.WALL, roomStatus, onRoomStatusUpdate);
            }
        }
    }

    /**
     * запускает и поддерживает жизненный цикл игры
     */
    public void refresh() {
        screen.update();
        crashHandlers.forEach(Runnable::run);
        pointsOnScreen.forEach(o -> o.render(screen));
    }

    private Coordinates generateUniqueRandomCoordinates() {
        Random random = new Random();
        Coordinates randomCoordinates = null;
        // если по случайно сгенерованным координатам уже что-то находится(или они ещё не сгенерированы)
        while (randomCoordinates == null || isGameObjectsContainsCoordinates(randomCoordinates))
            // генерируем новые координаты
            randomCoordinates = new Coordinates(random.nextInt(screen.getWidth()) + 1, random.nextInt(screen.getHeight()) + 1   );
        return randomCoordinates;
    }

    private boolean isGameObjectsContainsCoordinates(Coordinates coordinates) {
        return pointsOnScreen.stream()
                .anyMatch(p -> p.getCoordinates().equals(coordinates));
    }

    public List<Point> getPointsOnScreen() {
        return pointsOnScreen;
    }

    public Screen getScreen() {
        return screen;
    }

    public int getGarbageNumber() {
        return garbageNumber;
    }

    public int incrementCollectedGarbageCount() {
        return collectedGarbageCount.incrementAndGet();
    }
}
