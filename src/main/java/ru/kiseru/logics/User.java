package ru.kiseru.logics;

public record User(long id, String username) {

    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be empty");
        }
    }
}
