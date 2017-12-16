package com.tutorteam.logics.models;

import com.tutorteam.logics.auxiliary.Coordinates;
import com.tutorteam.logics.auxiliary.Type;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public abstract class Point {
    protected Coordinates coordinates;
    protected boolean isVisible;

    public Point(Coordinates coordinates) {
        this.coordinates = coordinates;
        isVisible = true;
    }

    abstract public Type getType();

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
