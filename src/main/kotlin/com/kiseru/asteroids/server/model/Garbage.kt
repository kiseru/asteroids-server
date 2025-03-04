package com.kiseru.asteroids.server.model

class Garbage(x: Int, y: Int) : GameObject(x, y) {

    override val type: Type = Type.GARBAGE

    override fun view(): String =
        "G"
}
