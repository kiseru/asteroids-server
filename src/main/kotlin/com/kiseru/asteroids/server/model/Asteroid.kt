package com.kiseru.asteroids.server.model

class Asteroid(coordinates: Coordinates) : Point(coordinates) {

    override val type: Type
        get() = Type.ASTEROID

    override val symbolToShow: String
        get() = "A"
}