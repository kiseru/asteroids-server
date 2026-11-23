package ru.kiseru.logics;

/**
 * Очки корабля.
 */
public record Score(int value) {

    public Score() {
        this(0);
    }

    /**
     * Возвращает новые очки, увеличенные на указанное количество.
     *
     * @param points количество очков для увеличения
     * @return новые очки
     */
    public Score increaseBy(int points) {
        return new Score(value + points);
    }

    /**
     * Возвращает новые очки, уменьшенные на указанное количество.
     *
     * @param points количество очков для уменьшения
     * @return новые очки
     * @throws IllegalStateException если текущее значение отрицательное
     */
    public Score decreaseBy(int points) {
        if (value < 0) {
            throw new IllegalStateException("cannot change score value if value < 0");
        }
        return new Score(value - points);
    }
}
