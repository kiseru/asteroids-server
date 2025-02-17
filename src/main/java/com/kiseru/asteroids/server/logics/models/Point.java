package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.logics.auxiliary.Type;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public abstract class Point {
    protected Coordinates coordinates;
    protected boolean isVisible = true;

    public Point(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    abstract public String view();

    abstract public Type getType();

    public void destroy() {
        this.isVisible = false;
    }

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
