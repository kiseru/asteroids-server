package com.kiseru.asteroids.server.model

class Asteroid(
    override val x: Int,
    override val y: Int
) : GameObject {

    override val type: Type = Type.ASTEROID

    override fun view(): String =
        "A"
}
