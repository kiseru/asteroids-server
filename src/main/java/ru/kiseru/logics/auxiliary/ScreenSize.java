package ru.kiseru.logics.auxiliary;

/**
 * Размер экрана.
 *
 * @param width ширина экрана, не меньше 1
 * @param height высота экрана, не меньше 1
 */
public record ScreenSize(int width, int height) {

    private static final int MIN_VALUE = 1;

    public ScreenSize {
        if (width < MIN_VALUE) {
            throw new IllegalArgumentException("width must be greater or equal than %d".formatted(MIN_VALUE));
        }

        if (height < MIN_VALUE) {
            throw new IllegalArgumentException("height must be greater or equal than %d".formatted(MIN_VALUE));
        }
    }
}
