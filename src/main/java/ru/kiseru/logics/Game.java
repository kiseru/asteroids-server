package ru.kiseru.logics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import ru.kiseru.logics.auxiliary.Position;
import ru.kiseru.logics.auxiliary.Type;
import ru.kiseru.logics.handlers.SpaceshipCrashHandler;
import ru.kiseru.logics.models.*;
import ru.kiseru.server.UserHandler;

public class Game {

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
     * Создает и регистрирует новый корабль
     *
     * @param userHandler - юзер, для которого регистрируется корабль
     */
    public void registerSpaceshipForUser(UserHandler userHandler, UserId userId) {
        var spaceship = new Spaceship(generateUniqueRandomCoordinates(), userId);
        userHandler.setSpaceship(spaceship);
        this.pointsOnScreen.add(spaceship);
        crashHandlers.add(new SpaceshipCrashHandler(this, userHandler, spaceship));
        spaceship.setCourseChecker(new CourseChecker(spaceship, pointsOnScreen, screen.getSize()));
    }

    /**
     * Запускает и поддерживает жизненный цикл игры
     */
    public synchronized void refresh() {
        screen.update();
        crashHandlers.forEach(SpaceshipCrashHandler::check);
        pointsOnScreen.forEach(screen::drawPoint);
    }

    public boolean isAnyoneAlive() {
        return pointsOnScreen.stream()
            .filter(point -> point.getType() == Type.SPACESHIP)
            .anyMatch(point -> point instanceof Spaceship s && s.getStatus() == Spaceship.Status.ALIVE);
    }

    private Position generateUniqueRandomCoordinates() {
        Random random = new Random();
        Position randomPosition = null;
        // если по случайно созданным координатам уже что-то находится (или они ещё не сгенерированы)
        while (randomPosition == null || isGameObjectsContainsCoordinates(randomPosition))
            // генерируем новые координаты
            randomPosition = new Position(random.nextInt(screen.getSize().width()) + 1, random.nextInt(screen.getSize().height()) + 1);
        return randomPosition;
    }

    private boolean isGameObjectsContainsCoordinates(Position position) {
        return pointsOnScreen.stream()
                .anyMatch(p -> p.getPosition().equals(position));
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