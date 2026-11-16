package ru.kiseru.logics.models;

import ru.kiseru.logics.auxiliary.Coordinates;
import ru.kiseru.logics.auxiliary.Type;

public abstract class Point {

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
