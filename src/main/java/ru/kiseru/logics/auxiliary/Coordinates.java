package ru.kiseru.logics.auxiliary;

public class Coordinates {

    private final int x;
    private final int y;

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
