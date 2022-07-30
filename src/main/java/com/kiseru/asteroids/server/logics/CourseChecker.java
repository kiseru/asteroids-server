package com.kiseru.asteroids.server.logics;

import com.kiseru.asteroids.server.model.Coordinates;
import com.kiseru.asteroids.server.model.Type;
import com.kiseru.asteroids.server.model.Point;
import com.kiseru.asteroids.server.model.SpaceShip;

import java.util.List;

/**
 * @author Bulat Giniyatullin
 * 10 Декабрь 2017
 */

public class CourseChecker {
    private SpaceShip spaceShip;
    private List<Point> pointsOnMap;
    private Screen screen;

    public CourseChecker(SpaceShip spaceShip, List<Point> pointsOnMap, Screen screen) {
        this.spaceShip = spaceShip;
        this.pointsOnMap = pointsOnMap;
        this.screen = screen;
    }

    public boolean isAsteroid() {
        List<Coordinates> asteroidsCoordinates = pointsOnMap.stream()
                .filter(p -> p.getType() == Type.ASTEROID)
                .map(Point::getCoordinates)
                .toList();
        return checkContaining(asteroidsCoordinates);
    }

    public boolean isGarbage() {
        List<Coordinates> garbageCoordinates = pointsOnMap.stream()
                .filter(p -> p.getType() == Type.GARBAGE)
                .map(Point::getCoordinates)
                .toList();
        return checkContaining(garbageCoordinates);
    }

    public boolean isWall() {
        switch (spaceShip.getDirection()) {
            case UP:
                return spaceShip.getY() == 1;
            case RIGHT:
                return spaceShip.getX() == screen.getWidth();
            case DOWN:
                return spaceShip.getY() == screen.getHeight();
            case LEFT:
                return spaceShip.getX() == 1;
        }
        return false;
    }

    private boolean checkContaining(List<Coordinates> container) {
        boolean contains = false;
        switch (spaceShip.getDirection()) {
            case UP:
                contains =  container
                        .contains(new Coordinates(spaceShip.getX(), spaceShip.getY() - 1));
                break;
            case RIGHT:
                contains =  container
                        .contains(new Coordinates(spaceShip.getX() + 1, spaceShip.getY()));
                break;
            case DOWN:
                contains =  container
                        .contains(new Coordinates(spaceShip.getX(), spaceShip.getY() + 1));
                break;
            case LEFT:
                contains =  container
                        .contains(new Coordinates(spaceShip.getX() - 1, spaceShip.getY()));
                break;
        }
        return contains;
    }
}
