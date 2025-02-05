package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.logics.auxiliary.Type;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public abstract class Point implements Model {
    protected Coordinates coordinates;
    protected boolean isVisible;

    public Point(Coordinates coordinates) {
        this.coordinates = coordinates;
        isVisible = true;
    }

    abstract public Type getType();

    public int getX() {
        return coordinates.x();
    }

    public int getY() {
        return coordinates.y();
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
