package ru.kiseru.logics;

import ru.kiseru.logics.auxiliary.Position;
import ru.kiseru.logics.auxiliary.ScreenSize;
import ru.kiseru.logics.auxiliary.Type;
import ru.kiseru.logics.models.Asteroid;
import ru.kiseru.logics.models.Crashable;
import ru.kiseru.logics.models.Garbage;
import ru.kiseru.logics.models.Point;
import ru.kiseru.logics.models.Spaceship;
import ru.kiseru.server.UserHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Game {

    private final List<Point> pointsOnScreen;
    private final Screen screen;
    private final ScreenSize screenSize;
    private final List<Spaceship> spaceships = new ArrayList<>();
    private final AtomicInteger collectedGarbageCount = new AtomicInteger(0);
    private final int garbageNumber;
    private final Runnable onAllGarbageCollected;

    /**
     * Конструктор создания игровой сессии
     * @param screen - экран
     * @param garbageNumber - количество мусора для первоначальной генерации
     * @param asteroidNumber - количество астероидов для первоначальной генерации
     */
    public Game(Screen screen, int garbageNumber, int asteroidNumber) {
        this(screen, garbageNumber, asteroidNumber, () -> {});
    }

    /**
     * Конструктор создания игровой сессии с обработчиком сбора всего мусора
     * @param screen - экран
     * @param garbageNumber - количество мусора для первоначальной генерации
     * @param asteroidNumber - количество астероидов для первоначальной генерации
     * @param onAllGarbageCollected - действие, выполняемое при сборе всего мусора
     */
    public Game(Screen screen, int garbageNumber, int asteroidNumber, Runnable onAllGarbageCollected) {
        this.garbageNumber = garbageNumber;
        this.screen = screen;
        this.screenSize = screen.getSize();
        this.pointsOnScreen = new ArrayList<>();
        this.onAllGarbageCollected = onAllGarbageCollected;

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
     * Создает корабль на свободных координатах, привязывает его к пользователю и регистрирует в игре
     *
     * @param userHandler - юзер, для которого регистрируется корабль
     * @param userId - идентификатор пользователя, которому принадлежит корабль
     */
    public void registerSpaceshipForUser(UserHandler userHandler, UserId userId) {
        var spaceship = new Spaceship(generateUniqueRandomCoordinates(), userId);
        userHandler.setSpaceship(spaceship);
        this.pointsOnScreen.add(spaceship);
        spaceships.add(spaceship);
    }

    /**
     * Выполняет один шаг игрового цикла: обновляет экран, проверяет столкновения всех кораблей и отрисовывает объекты
     */
    public synchronized void refresh() {
        screen.update();
        spaceships.forEach(this::check);
        pointsOnScreen.forEach(screen::drawPoint);
    }

    /**
     * Проверяет столкновения корабля с объектами на экране и со стеной и применяет последствия столкновения
     *
     * @param spaceship - корабль, для которого проверяются столкновения
     */
    private void check(Spaceship spaceship) {
        var isCollision = false;
        Point collisionPoint = null;
        for (var point : pointsOnScreen) {
            if (point.getType() != Type.SPACESHIP &&
                    point.isVisible() &&
                    point.getPosition().equals(spaceship.getPosition())) {
                isCollision = true;
                collisionPoint = point;
                break;
            }
        }
        if (isCollision) {
            crash(collisionPoint.getType(), spaceship);
            ((Crashable) collisionPoint).crash();
        } else {
            // проверка на столкновение со стеной
            if (!this.getScreen().getSize().isInBounds(spaceship.getPosition())) {
                crash(Type.WALL, spaceship);
            }
        }
    }

    /**
     * Вызывается при выявлении столкновения корабля с чем-либо
     *
     * @param type      - тип объекта, с которым произошло столкновение
     * @param spaceship - корабль, столкнувшийся с объектом
     */
    private synchronized void crash(Type type, Spaceship spaceship) {
        if (type == Type.ASTEROID) {
            spaceship.subtractScore();
        } else if (type == Type.GARBAGE) {
            spaceship.addScore();
            var collected = incrementCollectedGarbageCount();
            if (collected >= getGarbageNumber()) {
                onAllGarbageCollected.run();
            }
        } else if (type == Type.WALL) {
            // возвращаемся назад, чтобы не находится на стене
            var position = spaceship.getPosition();
            spaceship.setPosition(position.aheadOf(spaceship.getDirection().opposite()));
            spaceship.subtractScore();
        }
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

    public int incrementCollectedGarbageCount() {
        return collectedGarbageCount.incrementAndGet();
    }

    /**
     * Проверяет, что на позиции впереди корабля находится астероид
     *
     * @param spaceship - корабль, для которого проверяется курс
     * @return {@code true}, если следующий шаг приведёт к столкновению с астероидом
     */
    public boolean isAsteroidAheadOf(Spaceship spaceship) {
        var asteroidPositions = pointsOnScreen.stream()
                .filter(p -> p.getType() == Type.ASTEROID)
                .map(Point::getPosition)
                .collect(Collectors.toList());
        return checkContaining(spaceship, asteroidPositions);
    }

    /**
     * Проверяет, что на позиции впереди корабля находится мусор
     *
     * @param spaceship - корабль, для которого проверяется курс
     * @return {@code true}, если следующий шаг приведёт к сбору мусора
     */
    public boolean isGarbageAheadOf(Spaceship spaceship) {
        List<Position> garbagePositions = pointsOnScreen.stream()
                .filter(p -> p.getType() == Type.GARBAGE)
                .map(Point::getPosition)
                .collect(Collectors.toList());
        return checkContaining(spaceship, garbagePositions);
    }

    /**
     * Проверяет, что на позиции впереди корабля находится стена
     *
     * @param spaceship - корабль, для которого проверяется курс
     * @return {@code true}, если следующий шаг приведёт корабль к границе экрана
     */
    public boolean isWallAheadOf(Spaceship spaceship) {
        return switch (spaceship.getDirection()) {
            case UP -> spaceship.getY() == 1;
            case RIGHT -> spaceship.getX() == screenSize.width();
            case DOWN -> spaceship.getY() == screenSize.height();
            case LEFT -> spaceship.getX() == 1;
        };
    }

    /**
     * Проверяет, что позиция впереди корабля совпадает с одной из переданных позиций
     *
     * @param spaceship - корабль, для которого проверяется курс
     * @param positions - список позиций, с которыми сравнивается позиция впереди корабля
     * @return {@code true}, если позиция впереди корабля содержится в списке позиций
     */
    private boolean checkContaining(Spaceship spaceship, List<Position> positions) {
        var position = spaceship.positionAhead();
        return positions.contains(position);
    }
}
