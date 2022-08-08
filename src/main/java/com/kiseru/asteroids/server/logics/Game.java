package com.kiseru.asteroids.server.logics;

import com.kiseru.asteroids.server.User;
import com.kiseru.asteroids.server.logics.handlers.SpaceshipCrashHandler;
import com.kiseru.asteroids.server.model.*;
import com.kiseru.asteroids.server.service.impl.CourseCheckerServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Game {

    private static final Random random = new Random();

    private final List<Point> gameObjects;

    private final List<Point> pointsOnScreen;

    private final Screen screen;

    private final List<SpaceshipCrashHandler> crashHandlers;

    private final AtomicInteger collectedGarbageCount = new AtomicInteger(0);

    private final int garbageNumber;

    /**
     * Конструктор создания игровой сессии
     *
     * @param screen         - экран
     * @param garbageNumber  - количество мусора для первоначальной генерации
     * @param asteroidNumber - количество астероидов для первоначальной генерации
     */
    public Game(Screen screen, int garbageNumber, int asteroidNumber) {
        this.garbageNumber = garbageNumber;
        this.screen = screen;
        this.pointsOnScreen = new ArrayList<>();
        this.gameObjects = new ArrayList<>();
        this.crashHandlers = new ArrayList<>();

        // генерируем мусор
        for (int i = 0; i < garbageNumber; i++) {
            Garbage garbage = new Garbage(generateUniqueRandomCoordinates());
            pointsOnScreen.add(garbage);
            gameObjects.add(garbage);
        }
        // генерируем астероиды
        for (int i = 0; i < asteroidNumber; i++) {
            Asteroid asteroid = new Asteroid(generateUniqueRandomCoordinates());
            pointsOnScreen.add(asteroid);
            gameObjects.add(asteroid);
        }
    }

    /**
     * создает и регистрирует новый корабль
     *
     * @param user - юзер, для которого регистриуется корабль
     */
    public void registerSpaceshipForUser(User user) {
        var courseCheckerService = new CourseCheckerServiceImpl(pointsOnScreen, screen);
        Spaceship spaceship = new Spaceship(user, courseCheckerService, generateUniqueRandomCoordinates());
        courseCheckerService.setSpaceship(spaceship);
        pointsOnScreen.add(spaceship);
        gameObjects.add(spaceship);
        crashHandlers.add(new SpaceshipCrashHandler(this, spaceship));
        user.setSpaceship(spaceship);
    }

    /**
     * запускает и поддерживает жизненный цикл игры
     */
    public void refresh() {
        screen.update();
        crashHandlers.forEach(SpaceshipCrashHandler::check);
        gameObjects.forEach(o -> o.render(screen));
    }

    private Coordinates generateUniqueRandomCoordinates() {
        Coordinates randomCoordinates = null;
        // если по случайно сгенерованным координатам уже что-то находится(или они ещё не сгенерированы)
        while (randomCoordinates == null || isGameObjectsContainsCoordinates(randomCoordinates))
            // генерируем новые координаты
            randomCoordinates = new Coordinates(random.nextInt(screen.getWidth()) + 1, random.nextInt(screen.getHeight()) + 1);
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