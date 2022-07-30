package com.kiseru.asteroids.server.model

class Garbage(coordinates: Coordinates) : Point(coordinates) {

    override val type: Type
        get() = Type.GARBAGE

    override val symbolToShow: String
        get() = "G"
}