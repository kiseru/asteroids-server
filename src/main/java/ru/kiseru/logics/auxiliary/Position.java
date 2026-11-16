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
}
