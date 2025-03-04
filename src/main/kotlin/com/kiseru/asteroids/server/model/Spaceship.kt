package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.logics.auxiliary.Coordinates
import com.kiseru.asteroids.server.logics.auxiliary.Direction
import com.kiseru.asteroids.server.logics.auxiliary.Type
import com.kiseru.asteroids.server.logics.models.Point

class Spaceship(
    coordinates: Coordinates,
    val user: User,
) : Point(coordinates) {

    var direction = Direction.UP
    var steps = 0
    var score = 100
    var isAlive = true

    override fun view(): String =
        user.id.toString()

    override fun getType(): Type =
        Type.SPACESHIP

    fun addScore() {
        score += 10
    }

    fun subtractScore() {
        score -= 50
        isAlive = score >= 0
    }
}
