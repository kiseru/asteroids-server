package com.kiseru.asteroids.server.model

class User(
    val id: Int,
    val username: String,
) {

    lateinit var spaceship: Spaceship

    override fun toString(): String {
        return String.format("%s %d", username, spaceship.score)
    }
}
