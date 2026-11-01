package com.kiseru.asteroids.server.model

class Asteroid(
    override val position: Position
) : GameObject {

    override val type: Type = Type.ASTEROID
}
