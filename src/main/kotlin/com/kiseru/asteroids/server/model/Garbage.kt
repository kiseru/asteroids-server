package com.kiseru.asteroids.server.model

class Garbage(x: Int, y: Int) : Point(x, y) {

    override val type: Type
        get() = Type.GARBAGE

    override val symbolToShow: String
        get() = "G"
}