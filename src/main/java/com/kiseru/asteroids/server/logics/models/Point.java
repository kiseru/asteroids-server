package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.model.Type;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public abstract class Point {
    protected Coordinates coordinates;
    protected boolean isVisible;

    protected Point(Coordinates coordinates) {
        this.coordinates = coordinates;
        isVisible = true;
    }

    public abstract Type getType();

    public int getX() {
        return coordinates.getX();
    }

    public int getY() {
        return coordinates.getY();
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
