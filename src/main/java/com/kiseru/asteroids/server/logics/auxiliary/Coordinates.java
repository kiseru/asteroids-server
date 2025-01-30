package com.kiseru.asteroids.server.logics.auxiliary;

/**
 * @author Bulat Giniyatullin
 * 09 Декабрь 2017
 */

public record Coordinates(int x, int y) {

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Coordinates && this.x == ((Coordinates) obj).x && this.y == ((Coordinates) obj).y;
    }
}
