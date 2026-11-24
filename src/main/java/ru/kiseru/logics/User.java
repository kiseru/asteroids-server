package ru.kiseru.logics;

/**
 * Пользователь игры.
 *
 * @param id       идентификатор пользователя
 * @param username имя пользователя
 */
public record User(UserId id, String username) {

    /**
     * Создаёт пользователя.
     *
     * @throws IllegalArgumentException если имя пользователя пустое
     */
    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be empty");
        }
    }
}
