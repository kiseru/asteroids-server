package ru.kiseru.logics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import ru.kiseru.logics.auxiliary.Coordinates;
import ru.kiseru.logics.auxiliary.Type;
import ru.kiseru.logics.handlers.SpaceshipCrashHandler;
import ru.kiseru.logics.models.*;
import ru.kiseru.server.User;

public class Game {

    private final List<Model> gameObjects;
    private final List<Point> pointsOnScreen;
    private final Screen screen;
    private final List<SpaceshipCrashHandler> crashHandlers;
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
     * Создает и регистрирует новый корабль
     * @param user - юзер, для которого регистрируется корабль
     */
    public void registerSpaceShipForUser(User user) {
        Spaceship spaceShip = new Spaceship(generateUniqueRandomCoordinates(), user);
        user.setSpaceship(spaceShip);
        this.pointsOnScreen.add(spaceShip);
        this.gameObjects.add(spaceShip);
        crashHandlers.add(new SpaceshipCrashHandler(this, spaceShip));
        spaceShip.setCourseChecker(new CourseChecker(spaceShip, this.pointsOnScreen, this.screen));
    }

    /**
     * Запускает и поддерживает жизненный цикл игры
     */
    public void refresh() {
        screen.update();
        crashHandlers.forEach(SpaceshipCrashHandler::check);
        gameObjects.forEach(o -> o.render(screen));
    }

    public boolean isAnyoneAlive() {
        return pointsOnScreen.stream()
            .filter(point -> point.getType() == Type.SPACESHIP)
            .anyMatch(point -> point instanceof Spaceship s && s.isOwnerAlive());
    }

    private Coordinates generateUniqueRandomCoordinates() {
        Random random = new Random();
        Coordinates randomCoordinates = null;
        // если по случайно созданным координатам уже что-то находится (или они ещё не сгенерированы)
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

    public List<SpaceshipCrashHandler> getCrashHandlers() {
        return crashHandlers;
    }

    public int incrementCollectedGarbageCount() {
        return collectedGarbageCount.incrementAndGet();
    }
}