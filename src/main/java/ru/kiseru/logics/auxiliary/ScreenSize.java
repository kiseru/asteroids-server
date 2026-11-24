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
    
    /**
     * Проверяет, находится ли позиция в пределах экрана.
     *
     * @param position проверяемая позиция
     * @return {@code true}, если позиция находится в пределах экрана, иначе {@code false}
     */
    public boolean isInBounds(Position position) {
        return position.x() >= 1
                && position.y() >= 1
                && position.x() <= width
                && position.y() <= height;
    }
}
