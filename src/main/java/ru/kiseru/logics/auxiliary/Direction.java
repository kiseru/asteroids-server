package ru.kiseru.logics.auxiliary;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    ;

    /**
     * Возвращает направление противоположное текущему.
     *
     * @return противоположное направление
     */
    public Direction opposite() {
        return switch (this) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }
}
