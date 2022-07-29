package com.kiseru.asteroids.server.logics.auxiliary;

/**
 * @author Bulat Giniyatullin
 * 09 Декабрь 2017
 */

public class Coordinates {
    private int x;
    private int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Coordinates && this.x == ((Coordinates) obj).x && this.y == ((Coordinates) obj).y;
    }
}
