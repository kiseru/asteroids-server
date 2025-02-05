package com.kiseru.asteroids.server.logics;

import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.logics.auxiliary.Type;
import com.kiseru.asteroids.server.logics.models.Asteroid;
import com.kiseru.asteroids.server.logics.models.Crashable;
import com.kiseru.asteroids.server.logics.models.Garbage;
import com.kiseru.asteroids.server.logics.models.Point;
import com.kiseru.asteroids.server.logics.models.Spaceship;
import com.kiseru.asteroids.server.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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

    /**
     * создает и регистрирует новый корабль
     * @param user - юзер, для которого регистриуется корабль
     */
    public void registerSpaceShipForUser(User user) {
        Spaceship spaceShip = new Spaceship(generateUniqueRandomCoordinates(), user);
        user.setSpaceShip(spaceShip);
        pointsOnScreen.add(spaceShip);
        crashHandlers.add(() -> check(this, spaceShip));
        spaceShip.setCourseChecker(new CourseChecker(spaceShip, this.pointsOnScreen, this.screen));
    }

    private void check(Game game, Spaceship spaceShip) {
        List<Point> points = game.getPointsOnScreen();
        Point collisionPoint = null;
        for (Point point: points) {
            if (point.getType() != Type.SPACESHIP &&
                    point.isVisible() &&
                    point.getCoordinates().equals(spaceShip.getCoordinates())) {
                collisionPoint = point;
                break;
            }
        }
        if (collisionPoint != null) {
            spaceShip.crash(collisionPoint.getType());
            ((Crashable)collisionPoint).crash();
        } else {
            // проверка на столкновение со стеной
            if (spaceShip.getX() == 0 || spaceShip.getY() == 0 ||
                    spaceShip.getX() > game.getScreen().getWidth() ||
                    spaceShip.getY() > game.getScreen().getHeight()) {
                spaceShip.crash(Type.WALL);
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
