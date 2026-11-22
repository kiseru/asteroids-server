package ru.kiseru.logics;

/**
 * Количество шагов корабля.
 */
public class Steps {

    private static final int MAX_VALUE = 1500;

    private final int value;

    public Steps() {
        this.value = 0;
    }

    private Steps(int value) {
        this.value = value;
    }

    /**
     * Возвращает новое количество шагов, увеличенное на один.
     *
     * @return новое количество шагов
     * @throws IllegalStateException если достигнут предел шагов
     */
    public Steps increment() {
        if (value == MAX_VALUE) {
            throw new IllegalStateException("steps value must be less than %d".formatted(MAX_VALUE));
        }
        return new Steps(value + 1);
    }

    public int getValue() {
        return value;
    }
}
