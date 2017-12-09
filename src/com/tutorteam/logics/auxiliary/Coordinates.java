package com.tutorteam.logics.auxiliary;

/**
 * @author Bulat Giniyatullin
 * 09 Декабрь 2017
 */

public class Coordinates {
    public int x;
    public int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Coordinates && this.x == ((Coordinates) obj).x && this.y == ((Coordinates) obj).y;
    }
}
