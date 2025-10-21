package com.kiseru.asteroids.server.model

data class Player(
    var direction: Direction = Direction.UP,
    var steps: Int = 0,
    var score: Int = 100,
    var isAlive: Boolean = true,
)
