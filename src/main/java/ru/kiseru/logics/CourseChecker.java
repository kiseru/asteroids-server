package ru.kiseru.logics;

import java.util.List;
import java.util.stream.Collectors;
import ru.kiseru.logics.auxiliary.Position;
import ru.kiseru.logics.auxiliary.Type;
import ru.kiseru.logics.models.Point;
import ru.kiseru.logics.models.Spaceship;

public class CourseChecker {

    private final Spaceship spaceship;
    private final List<Point> pointsOnMap;
    private final Screen screen;

    public CourseChecker(Spaceship spaceship, List<Point> pointsOnMap, Screen screen) {
        this.spaceship = spaceship;
        this.pointsOnMap = pointsOnMap;
        this.screen = screen;
    }

    public boolean isAsteroid() {
        List<Position> asteroidsCoordinates = pointsOnMap.stream()
                .filter(p -> p.getType() == Type.ASTEROID)
                .map(Point::getPosition)
                .collect(Collectors.toList());
        return checkContaining(asteroidsCoordinates);
    }

    public boolean isGarbage() {
        List<Position> garbageCoordinates = pointsOnMap.stream()
                .filter(p -> p.getType() == Type.GARBAGE)
                .map(Point::getPosition)
                .collect(Collectors.toList());
        return checkContaining(garbageCoordinates);
    }

    public boolean isWall() {
        return switch (spaceship.getDirection()) {
            case UP -> spaceship.getY() == 1;
            case RIGHT -> spaceship.getX() == screen.getSize().width();
            case DOWN -> spaceship.getY() == screen.getSize().height();
            case LEFT -> spaceship.getX() == 1;
        };
    }

    private boolean checkContaining(List<Position> container) {
        var coordinates = switch (spaceship.getDirection()) {
            case UP -> new Position(spaceship.getX(), spaceship.getY() - 1);
            case RIGHT -> new Position(spaceship.getX() + 1, spaceship.getY());
            case DOWN -> new Position(spaceship.getX(), spaceship.getY() + 1);
            case LEFT -> new Position(spaceship.getX() - 1, spaceship.getY());
        };

        return container.contains(coordinates);
    }
}
