package com.kiseru.asteroids.server.factory.impl

import com.kiseru.asteroids.server.factory.GameFactory
import com.kiseru.asteroids.server.logics.Game
import com.kiseru.asteroids.server.model.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameFactoryImpl(
    @Value("\${asteroids.screen.height}") private val screenHeight: Int,
    @Value("\${asteroids.screen.width}") private val screenWidth: Int,
    @Value("\${asteroids.number-of-asteroid-cells}") private val numberOfAsteroidCells: Int,
    @Value("\${asteroids.number-of-garbage-cells}") private val numberOfGarbageCells: Int,
) : GameFactory {

    override fun createGame(screen: Screen): Game {
        val pointsOnScreen = mutableListOf<Point>()
        val gameObjects = mutableListOf<Point>()
        generateAsteroids(pointsOnScreen, gameObjects)
        generateGarbage(pointsOnScreen, gameObjects)
        return Game(screen, numberOfGarbageCells, pointsOnScreen, gameObjects)
    }

    private fun generateAsteroids(pointsOnScreen: MutableList<Point>, gameObjects: MutableList<Point>) {
        for (i in 0 until numberOfAsteroidCells) {
            val asteroid = Asteroid(generateUniqueRandomCoordinates(pointsOnScreen))
            pointsOnScreen.add(asteroid)
            gameObjects.add(asteroid)
        }
    }

    private fun generateGarbage(pointsOnScreen: MutableList<Point>, gameObjects: MutableList<Point>) {
        for (i in 0 until numberOfGarbageCells) {
            val garbage = Garbage(generateUniqueRandomCoordinates(pointsOnScreen))
            pointsOnScreen.add(garbage)
            gameObjects.add(garbage)
        }
    }

    private fun generateUniqueRandomCoordinates(pointsOnScreen: MutableList<Point>): Coordinates {
        var randomCoordinates = generateCoordinates()
        while (isGameObjectsContainsCoordinates(randomCoordinates, pointsOnScreen)) {
            randomCoordinates = generateCoordinates()
        }
        return randomCoordinates
    }

    private fun generateCoordinates(): Coordinates =
        Coordinates(Random.nextInt(screenWidth) + 1, Random.nextInt(screenHeight) + 1)

    private fun isGameObjectsContainsCoordinates(
        coordinates: Coordinates,
        pointsOnScreen: MutableList<Point>
    ): Boolean = pointsOnScreen.stream()
        .anyMatch { p: Point -> p.coordinates == coordinates }
}