package com.kiseru.asteroids.server.logics.auxiliary;

import java.util.Objects;


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
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Coordinates coordinates)) {
            return false;
        }

        return x == coordinates.x && y == coordinates.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
