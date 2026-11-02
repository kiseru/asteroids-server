package ru.kiseru.logics.handlers;

import ru.kiseru.logics.Game;
import ru.kiseru.logics.auxiliary.Type;
import ru.kiseru.logics.models.Crashable;
import ru.kiseru.logics.models.Point;
import ru.kiseru.logics.models.SpaceShip;

import java.util.List;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class SpaceShipCrashHandler {
    private SpaceShip spaceShip;
    private Game game;

    public SpaceShipCrashHandler(Game game, SpaceShip spaceShip) {
        this.spaceShip = spaceShip;
        this.game = game;
    }

    public void check() {
        List<Point> points = game.getPointsOnScreen();
        boolean isCollision = false;
        Point collisionPoint = null;
        for (Point point: points) {
            if (point.getType() != Type.SPACESHIP &&
                    point.isVisible() &&
                    point.getCoordinates().equals(spaceShip.getCoordinates())) {
                isCollision = true;
                collisionPoint = point;
                break;
            }
        }
        if (isCollision) {
            spaceShip.crash(collisionPoint.getType());
            ((Crashable)collisionPoint).crash();
        } else {
            // проверка на столкновение со стеной
            if (spaceShip.getX() == 0 || spaceShip.getY() == 0 ||
                    spaceShip.getX() > game.getScreen().getWidth() ||
                    spaceShip.getY() > game.getScreen().getHeight()) {
                spaceShip.crash(Type.WALL);
            }
        }
    }
}
