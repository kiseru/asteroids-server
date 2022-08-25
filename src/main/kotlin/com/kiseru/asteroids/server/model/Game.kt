package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.handler.SpaceshipCrashHandler
import com.kiseru.asteroids.server.handler.impl.SpaceshipCrashHandlerImpl
import com.kiseru.asteroids.server.service.impl.CourseCheckerServiceImpl
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class Game(
    val screen: Screen,
    val garbageNumber: Int,
    val pointsOnScreen: MutableList<Point>,
    private val gameObjects: MutableList<Point>,
) {

    private val crashHandlers = mutableListOf<SpaceshipCrashHandler>()

    private val collectedGarbageCount = AtomicInteger(0)

    fun registerSpaceshipForUser(user: User) {
        val courseCheckerService = CourseCheckerServiceImpl(pointsOnScreen, screen)
        val spaceship = Spaceship(user, courseCheckerService, generateUniqueRandomCoordinates())
        courseCheckerService.spaceship = spaceship
        pointsOnScreen.add(spaceship)
        gameObjects.add(spaceship)
        crashHandlers.add(SpaceshipCrashHandlerImpl(this, spaceship))
        user.spaceship = spaceship
    }

    fun refresh() {
        screen.update()
        for (crashHandler in crashHandlers) {
            crashHandler.check()
        }

        for (gameObject in gameObjects) {
            gameObject.render(screen)
        }
    }

    fun showField() = screen.display()

    fun incrementCollectedGarbageCount(): Int = collectedGarbageCount.getAndIncrement()

    private fun generateUniqueRandomCoordinates(): Coordinates {
        var randomCoordinates = generateCoordinates()
        while (isGameObjectsContainsCoordinates(randomCoordinates)) {
            randomCoordinates = generateCoordinates()
        }
        return randomCoordinates
    }

    private fun generateCoordinates(): Coordinates =
        Coordinates(Random.nextInt(screen.width) + 1, Random.nextInt(screen.height) + 1)

    private fun isGameObjectsContainsCoordinates(coordinates: Coordinates): Boolean =
        pointsOnScreen.any { it.coordinates == coordinates }
}