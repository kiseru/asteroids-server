package com.kiseru.asteroids.server.logics;

import com.kiseru.asteroids.server.logics.models.Point;
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.logics.auxiliary.Type;
import com.kiseru.asteroids.server.logics.models.Spaceship;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bulat Giniyatullin
 * 10 Декабрь 2017
 */

public class CourseChecker {
    private final List<Point> pointsOnMap;
    private final Screen screen;

    public CourseChecker(List<Point> pointsOnMap, Screen screen) {
        this.pointsOnMap = pointsOnMap;
        this.screen = screen;
    }

    public boolean isAsteroid(Spaceship spaceship) {
        List<Coordinates> asteroidsCoordinates = pointsOnMap.stream()
                .filter(p -> p.getType() == Type.ASTEROID)
                .map(Point::getCoordinates)
                .collect(Collectors.toList());
        return checkContaining(spaceship, asteroidsCoordinates);
    }

    public boolean isGarbage(Spaceship spaceship) {
        List<Coordinates> garbageCoordinates = pointsOnMap.stream()
                .filter(p -> p.getType() == Type.GARBAGE)
                .map(Point::getCoordinates)
                .collect(Collectors.toList());
        return checkContaining(spaceship, garbageCoordinates);
    }

    public boolean isWall(Spaceship spaceship) {
        return switch (spaceship.getDirection()) {
            case UP -> spaceship.getY() == 1;
            case RIGHT -> spaceship.getX() == screen.getWidth();
            case DOWN -> spaceship.getY() == screen.getHeight();
            case LEFT -> spaceship.getX() == 1;
        };
    }

    private boolean checkContaining(Spaceship spaceship, List<Coordinates> container) {
        return switch (spaceship.getDirection()) {
            case UP -> container
                    .contains(new Coordinates(spaceship.getX(), spaceship.getY() - 1));
            case RIGHT -> container
                    .contains(new Coordinates(spaceship.getX() + 1, spaceship.getY()));
            case DOWN -> container
                    .contains(new Coordinates(spaceship.getX(), spaceship.getY() + 1));
            case LEFT -> container
                    .contains(new Coordinates(spaceship.getX() - 1, spaceship.getY()));
        };
    }
}
