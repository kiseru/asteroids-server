package com.kiseru.asteroids.server.factory.impl

import com.kiseru.asteroids.server.factory.GameFactory
import com.kiseru.asteroids.server.model.*
import com.kiseru.asteroids.server.properties.AsteroidsProperties
import com.kiseru.asteroids.server.service.CoordinateService
import org.springframework.stereotype.Component

@Component
class GameFactoryImpl(
    private val asteroidsProperties: AsteroidsProperties,
    private val coordinateService: CoordinateService,
) : GameFactory {

    override fun createGame(screen: Screen): Game {
        val pointsOnScreen = mutableListOf<Point>()
        val gameObjects = mutableListOf<Point>()
        generateAsteroids(pointsOnScreen, gameObjects)
        generateGarbage(pointsOnScreen, gameObjects)
        return Game(screen, asteroidsProperties.numberOfGarbageCells, pointsOnScreen, gameObjects, coordinateService)
    }

    private fun generateAsteroids(pointsOnScreen: MutableList<Point>, gameObjects: MutableList<Point>) {
        for (i in 0 until asteroidsProperties.numberOfAsteroidCells) {
            val (x, y) = generateUniqueRandomCoordinates(pointsOnScreen)
            val asteroid = Asteroid(x, y)
            pointsOnScreen.add(asteroid)
            gameObjects.add(asteroid)
        }
    }

    private fun generateGarbage(pointsOnScreen: MutableList<Point>, gameObjects: MutableList<Point>) {
        for (i in 0 until asteroidsProperties.numberOfGarbageCells) {
            val (x, y) = generateUniqueRandomCoordinates(pointsOnScreen)
            val garbage = Garbage(x, y)
            pointsOnScreen.add(garbage)
            gameObjects.add(garbage)
        }
    }

    private fun generateUniqueRandomCoordinates(pointsOnScreen: MutableList<Point>): Pair<Int, Int> =
        coordinateService.generateCoordinateSequence()
            .dropWhile { isGameObjectsContainsCoordinates(it.first, it.second, pointsOnScreen) }
            .first()

    private fun isGameObjectsContainsCoordinates(x: Int, y: Int, pointsOnScreen: MutableList<Point>): Boolean =
        pointsOnScreen.any { it.x == x && it.y == y }
}