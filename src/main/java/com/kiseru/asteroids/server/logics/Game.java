package com.kiseru.asteroids.server.logics;

import com.kiseru.asteroids.server.User;
import com.kiseru.asteroids.server.handler.SpaceshipCrashHandler;
import com.kiseru.asteroids.server.handler.impl.SpaceshipCrashHandlerImpl;
import com.kiseru.asteroids.server.model.Coordinates;
import com.kiseru.asteroids.server.model.Point;
import com.kiseru.asteroids.server.model.Screen;
import com.kiseru.asteroids.server.model.Spaceship;
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

    private final List<SpaceshipCrashHandler> crashHandlers = new ArrayList<>();

    private final AtomicInteger collectedGarbageCount = new AtomicInteger(0);

    private final int garbageNumber;

    public Game(Screen screen, int garbageNumber, List<Point> pointsOnScreen, List<Point> gameObjects) {
        this.garbageNumber = garbageNumber;
        this.screen = screen;
        this.pointsOnScreen = pointsOnScreen;
        this.gameObjects = gameObjects;
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
        crashHandlers.add(new SpaceshipCrashHandlerImpl(this, spaceship));
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
        Coordinates randomCoordinates = generateCoordinates();
        while (isGameObjectsContainsCoordinates(randomCoordinates)) {
            randomCoordinates = generateCoordinates();
        }
        return randomCoordinates;
    }

    private Coordinates generateCoordinates() {
        return new Coordinates(random.nextInt(screen.getWidth()) + 1,
                               random.nextInt(screen.getHeight()) + 1);
    }

    private boolean isGameObjectsContainsCoordinates(Coordinates coordinates) {
        return pointsOnScreen.stream()
                .anyMatch(p -> p.getCoordinates().equals(coordinates));
    }

    public void showField() {
        screen.display();
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