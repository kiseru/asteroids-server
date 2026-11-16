package ru.kiseru.logics.models;

import ru.kiseru.logics.auxiliary.Position;
import ru.kiseru.logics.auxiliary.Type;

public abstract class Point {

    protected Position position;
    protected boolean isVisible;

    public Point(Position position) {
        this.position = position;
        isVisible = true;
    }

    abstract public Type getType();

    public int getX() {
        return position.x();
    }

    public int getY() {
        return position.y();
    }

    public Position getCoordinates() {
        return position;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
