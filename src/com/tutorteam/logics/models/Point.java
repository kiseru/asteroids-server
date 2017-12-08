package com.tutorteam.logics.models;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public abstract class Point {
    protected int x;
    protected int y;
    protected boolean isVisible;

    abstract public Type getType();

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
