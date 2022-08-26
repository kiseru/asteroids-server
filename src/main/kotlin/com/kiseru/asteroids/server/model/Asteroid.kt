package com.kiseru.asteroids.server.model

class Asteroid(x: Int, y: Int) : Point(x, y) {

    override val type: Type
        get() = Type.ASTEROID

    override val symbolToShow: String
        get() = "A"
}