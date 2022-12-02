package com.kiseru.asteroids.server.model

class Garbage(x: Int, y: Int) : Point(x, y) {

    override val type: Type = Type.GARBAGE

    override val symbolToShow: String = "G"
}