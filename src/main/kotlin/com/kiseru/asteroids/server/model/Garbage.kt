package com.kiseru.asteroids.server.model

class Garbage(
    override val x: Int,
    override val y: Int,
) : GameObject {

    override val type: Type = Type.GARBAGE

    override fun view(): String =
        "G"
}
