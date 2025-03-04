package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.logics.Screen
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates
import com.kiseru.asteroids.server.logics.auxiliary.Direction
import com.kiseru.asteroids.server.logics.auxiliary.Type
import com.kiseru.asteroids.server.logics.models.Point
import java.util.Random
import java.util.concurrent.atomic.AtomicInteger

class Game(
    val screen: Screen,
    val garbageNumber: Int,
) {

    val points = mutableListOf<Point>()
    private val collectedGarbageCount = AtomicInteger(0)

    fun refresh() {
        screen.update()
        points.forEach(screen::render)
    }

    fun generateUniqueRandomCoordinates(): Coordinates {
        val random = Random()
        var randomCoordinates: Coordinates? = null
        while (randomCoordinates == null || isGameObjectsContainsCoordinates(randomCoordinates)) {
            randomCoordinates = Coordinates(random.nextInt(screen.width) + 1, random.nextInt(screen.height) + 1)
        }

        return randomCoordinates
    }

    private fun isGameObjectsContainsCoordinates(coordinates: Coordinates): Boolean =
        points.any { it.coordinates == coordinates }

    fun addPoint(point: Point) {
        points.add(point)
    }

    fun incrementCollectedGarbageCount(): Int =
        collectedGarbageCount.incrementAndGet()

    fun isAsteroidAhead(spaceship: Spaceship): Boolean =
        points.any { it.type == Type.ASTEROID && isPointAhead(spaceship, it) }

    fun isGarbageAhead(spaceship: Spaceship): Boolean =
        points.any { it.type == Type.GARBAGE && isPointAhead(spaceship, it) }

    private fun isPointAhead(spaceship: Spaceship, point: Point): Boolean =
        when (spaceship.direction) {
            Direction.UP -> spaceship.x == point.x && spaceship.y == point.y + 1
            Direction.DOWN -> spaceship.x == point.x && spaceship.y == point.y - 1
            Direction.LEFT -> spaceship.x == point.x + 1 && spaceship.y == point.y
            Direction.RIGHT -> spaceship.x == point.x - 1 && spaceship.y == point.y
        }

    fun isWallAhead(spaceship: Spaceship): Boolean =
        when (spaceship.direction) {
            Direction.UP -> spaceship.y == 1
            Direction.DOWN -> spaceship.y == screen.height
            Direction.LEFT -> spaceship.x == 1
            Direction.RIGHT -> spaceship.x == screen.width
        }
}