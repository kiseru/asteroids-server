package com.kiseru.asteroids.server.model

class Asteroid(x: Int, y: Int) : GameObject(x, y) {

    override val type: Type = Type.ASTEROID

    override fun view(): String =
        "A"
}
