package com.kiseru.asteroids.server.model

class Asteroid(x: Int, y: Int) : Point(x, y) {

    override val type: Type = Type.ASTEROID

    override val symbolToShow: String = "A"
}