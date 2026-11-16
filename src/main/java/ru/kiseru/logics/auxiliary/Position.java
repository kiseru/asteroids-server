package ru.kiseru.logics.auxiliary;

public record Position(int x, int y) {

    private static final int MIN_VALUE = 0;

    public Position {
        if (x < MIN_VALUE) {
            throw new IllegalArgumentException("x must be greater or equal than %d".formatted(MIN_VALUE));
        }

        if (y < MIN_VALUE) {
            throw new IllegalArgumentException("y must be greater or equal than %d".formatted(MIN_VALUE));
        }
    }

    /**
     * Возвращает следующую позицию по переданному направлению.
     *
     * @param direction направление движения
     * @return следующая позиция
     */
    public Position aheadOf(Direction direction) {
        return switch (direction) {
            case UP -> new Position(x, y - 1);
            case DOWN -> new Position(x, y + 1);
            case LEFT -> new Position(x - 1, y);
            case RIGHT -> new Position(x + 1, y);
        };
    }
}
