package com.kiseru.asteroids.server.model

class Garbage(
    override val position: Position,
) : GameObject {

    override val type: Type = Type.GARBAGE

    override fun view(): String =
        "G"
}
