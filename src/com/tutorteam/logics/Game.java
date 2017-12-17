package com.tutorteam.logics;

import com.tutorteam.logics.auxiliary.Coordinates;
import com.tutorteam.logics.auxiliary.Type;
import com.tutorteam.logics.handlers.SpaceShipCrashHandler;
import com.tutorteam.logics.models.*;
import com.tutorteam.server.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Game {
    private List<Model> gameObjects;
    private List<Point> pointsOnScreen;
    private Screen screen;
    private List<SpaceShipCrashHandler> crashHandlers;
    private AtomicInteger collectedGarbageCount = new AtomicInteger(0);
    private int garbageNumber;

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
     * @param user - юзер, для которого регистриуется корабль
     */
    public void registerSpaceShipForUser(User user) {
        SpaceShip spaceShip = new SpaceShip(generateUniqueRandomCoordinates(), user);
        user.setSpaceShip(spaceShip);
        this.pointsOnScreen.add(spaceShip);
        this.gameObjects.add(spaceShip);
        crashHandlers.add(new SpaceShipCrashHandler(this, spaceShip));
        spaceShip.setCourseChecker(new CourseChecker(spaceShip, this.pointsOnScreen, this.screen));
    }

    /**
     * запускает и поддерживает жизненный цикл игры
     */
    public void refresh() {
        screen.update();
        crashHandlers.forEach(SpaceShipCrashHandler::check);
        gameObjects.forEach(o -> o.render(screen));
    }

    public boolean isAnyoneAlive() {
        return pointsOnScreen.stream()
                .filter(p -> p.getType() == Type.SPACESHIP)
                .map(s -> ((SpaceShip)s).isOwnerAlive())
                .reduce((b1, b2) -> b1 || b2)
                .get();
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

    public List<Model> getGameObjects() {
        return gameObjects;
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

    public List<SpaceShipCrashHandler> getCrashHandlers() {
        return crashHandlers;
    }

    public int incrementCollectedGarbageCount() {
        return collectedGarbageCount.incrementAndGet();
    }
}