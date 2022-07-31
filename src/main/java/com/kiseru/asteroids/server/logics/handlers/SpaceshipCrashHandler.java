package com.kiseru.asteroids.server.logics.handlers;

import com.kiseru.asteroids.server.logics.Game;
import com.kiseru.asteroids.server.model.Type;
import com.kiseru.asteroids.server.model.Point;
import com.kiseru.asteroids.server.model.Spaceship;

import java.util.List;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class SpaceshipCrashHandler {
    private Spaceship spaceship;
    private Game game;

    public SpaceshipCrashHandler(Game game, Spaceship spaceship) {
        this.spaceship = spaceship;
        this.game = game;
    }

    public void check() {
        List<Point> points = game.getPointsOnScreen();
        boolean isCollision = false;
        Point collisionPoint = null;
        for (Point point: points) {
            if (point.getType() != Type.SPACESHIP &&
                    point.isVisible() &&
                    point.getCoordinates().equals(spaceship.getCoordinates())) {
                isCollision = true;
                collisionPoint = point;
                break;
            }
        }
        if (isCollision) {
            spaceship.crash(collisionPoint.getType());
            collisionPoint.destroy();
        } else {
            // проверка на столкновение со стеной
            if (spaceship.getX() == 0 || spaceship.getY() == 0 ||
                    spaceship.getX() > game.getScreen().getWidth() ||
                    spaceship.getY() > game.getScreen().getHeight()) {
                spaceship.crash(Type.WALL);
            }
        }
    }
}
