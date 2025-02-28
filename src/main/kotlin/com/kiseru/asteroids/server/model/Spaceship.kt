package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.logics.Screen
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates
import com.kiseru.asteroids.server.logics.auxiliary.Direction
import com.kiseru.asteroids.server.logics.auxiliary.Type
import com.kiseru.asteroids.server.logics.models.Point

class Spaceship(
    coordinates: Coordinates,
    val user: User,
    private val pointsOnMap: List<Point>,
    private val screen: Screen,
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

    fun isAsteroidAhead(): Boolean =
        pointsOnMap.any { it.type == Type.ASTEROID && isPointAhead(it) }

    fun isGarbageAhead(): Boolean =
        pointsOnMap.any { it.type == Type.GARBAGE && isPointAhead(it) }

    private fun isPointAhead(point: Point): Boolean =
        when (direction) {
            Direction.UP -> x == point.x && y == point.y + 1
            Direction.DOWN -> x == point.x && y == point.y - 1
            Direction.LEFT -> x == point.x + 1 && y == point.y
            Direction.RIGHT -> x == point.x - 1 && y == point.y
        }

    fun isWallAhead(): Boolean =
        when (direction) {
            Direction.UP -> y == 1
            Direction.RIGHT -> x == screen.width
            Direction.DOWN -> y == screen.height
            Direction.LEFT -> x == 1
        }
}