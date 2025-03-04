package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.logics.Screen
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates
import com.kiseru.asteroids.server.logics.models.Point
import java.util.Random
import java.util.concurrent.atomic.AtomicInteger

class Game(
    val screen: Screen,
    val garbageNumber: Int,
) {

    val pointsOnScreen = mutableListOf<Point>()
    private val collectedGarbageCount = AtomicInteger(0)

    fun refresh() {
        screen.update()
        pointsOnScreen.forEach(screen::render)
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
        pointsOnScreen.any { it.coordinates == coordinates }

    fun addPoint(point: Point) {
        pointsOnScreen.add(point)
    }

    fun incrementCollectedGarbageCount(): Int =
        collectedGarbageCount.incrementAndGet()
}