package com.kiseru.asteroids.server.factory.impl

import com.kiseru.asteroids.server.factory.GameFactory
import com.kiseru.asteroids.server.logics.Game
import com.kiseru.asteroids.server.model.*
import com.kiseru.asteroids.server.properties.AsteroidsProperties
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameFactoryImpl(
    private val asteroidsProperties: AsteroidsProperties,
) : GameFactory {

    override fun createGame(screen: Screen): Game {
        val pointsOnScreen = mutableListOf<Point>()
        val gameObjects = mutableListOf<Point>()
        generateAsteroids(pointsOnScreen, gameObjects)
        generateGarbage(pointsOnScreen, gameObjects)
        return Game(screen, asteroidsProperties.numberOfGarbageCells, pointsOnScreen, gameObjects)
    }

    private fun generateAsteroids(pointsOnScreen: MutableList<Point>, gameObjects: MutableList<Point>) {
        for (i in 0 until asteroidsProperties.numberOfAsteroidCells) {
            val asteroid = Asteroid(generateUniqueRandomCoordinates(pointsOnScreen))
            pointsOnScreen.add(asteroid)
            gameObjects.add(asteroid)
        }
    }

    private fun generateGarbage(pointsOnScreen: MutableList<Point>, gameObjects: MutableList<Point>) {
        for (i in 0 until asteroidsProperties.numberOfGarbageCells) {
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

    private fun generateCoordinates(): Coordinates = Coordinates(
        Random.nextInt(asteroidsProperties.screen.width) + 1,
        Random.nextInt(asteroidsProperties.screen.height) + 1,
    )

    private fun isGameObjectsContainsCoordinates(
        coordinates: Coordinates,
        pointsOnScreen: MutableList<Point>
    ): Boolean = pointsOnScreen.stream()
        .anyMatch { p: Point -> p.coordinates == coordinates }
}