package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.logics.Screen
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates
import com.kiseru.asteroids.server.logics.auxiliary.Direction
import com.kiseru.asteroids.server.logics.auxiliary.Type
import com.kiseru.asteroids.server.logics.models.Point

class Spaceship(
    coordinates: Coordinates,
    private val ownerId: Int,
    private val pointsOnMap: List<Point>,
    private val screen: Screen,
) : Point(coordinates) {

    var direction = Direction.UP
    var steps = 0
    var score = 0
    var isAlive = true

    override fun view(): String =
        ownerId.toString()

    override fun getType(): Type =
        Type.SPACESHIP

    fun addScore() {
        score += 10
    }

    fun subtractScore() {
        score -= 50
        isAlive = score >= 0
    }

    fun isAsteroidAhead(): Boolean {
        val coordinates = pointsOnMap.asSequence()
            .filter { it.type == Type.ASTEROID }
            .map(Point::getCoordinates)
            .toList()
        return checkContaining(coordinates)
    }

    fun isGarbageAhead(): Boolean {
        val coordinates = pointsOnMap.asSequence()
            .filter { it.type == Type.GARBAGE }
            .map(Point::getCoordinates)
            .toList()
        return checkContaining(coordinates)
    }

    private fun checkContaining(coordinates: List<Coordinates>): Boolean =
        when (direction) {
            Direction.UP -> coordinates.contains(Coordinates(x, y - 1))
            Direction.RIGHT -> coordinates.contains(Coordinates(x + 1, y))
            Direction.DOWN -> coordinates.contains(Coordinates(x, y + 1))
            Direction.LEFT -> coordinates.contains(Coordinates(x - 1, y))
        }

    fun isWallAhead(): Boolean =
        when (direction) {
            Direction.UP -> y == 1
            Direction.RIGHT -> x == screen.width
            Direction.DOWN -> y == screen.height
            Direction.LEFT -> x == 1
        }
}