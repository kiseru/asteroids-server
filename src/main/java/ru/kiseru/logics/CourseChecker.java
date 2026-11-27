package ru.kiseru.logics;

import java.util.List;
import java.util.stream.Collectors;
import ru.kiseru.logics.auxiliary.Position;
import ru.kiseru.logics.auxiliary.ScreenSize;
import ru.kiseru.logics.auxiliary.Type;
import ru.kiseru.logics.models.Point;
import ru.kiseru.logics.models.Spaceship;

public class CourseChecker {

    private final Spaceship spaceship;
    private final List<Point> pointsOnMap;
    private final ScreenSize screenSize;

    public CourseChecker(Spaceship spaceship, List<Point> pointsOnMap, ScreenSize screenSize) {
        this.spaceship = spaceship;
        this.pointsOnMap = pointsOnMap;
        this.screenSize = screenSize;
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
            case RIGHT -> spaceship.getX() == screenSize.width();
            case DOWN -> spaceship.getY() == screenSize.height();
            case LEFT -> spaceship.getX() == 1;
        };
    }

    private boolean checkContaining(List<Position> container) {
        var position = spaceship.positionAhead();
        return container.contains(position);
    }
}
