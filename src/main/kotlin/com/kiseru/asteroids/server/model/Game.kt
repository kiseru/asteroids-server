package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.handler.SpaceshipCrashHandler
import com.kiseru.asteroids.server.handler.impl.SpaceshipCrashHandlerImpl
import com.kiseru.asteroids.server.service.CoordinateService
import com.kiseru.asteroids.server.service.impl.CourseCheckerServiceImpl
import java.util.concurrent.atomic.AtomicInteger

class Game(
    val screen: Screen,
    val garbageNumber: Int,
    val pointsOnScreen: MutableList<Point>,
    private val gameObjects: MutableList<Point>,
    private val coordinateService: CoordinateService,
) {

    private val crashHandlers = mutableListOf<SpaceshipCrashHandler>()

    private val collectedGarbageCount = AtomicInteger(0)

    fun registerSpaceshipForUser(user: User, room: Room): Spaceship {
        val courseCheckerService = CourseCheckerServiceImpl(pointsOnScreen, screen)
        val (x, y) = generateUniqueRandomCoordinates()
        val spaceship = Spaceship(user, room, courseCheckerService, x, y)
        courseCheckerService.spaceship = spaceship
        pointsOnScreen.add(spaceship)
        gameObjects.add(spaceship)
        crashHandlers.add(SpaceshipCrashHandlerImpl(this, spaceship))
        return spaceship
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

    private fun generateUniqueRandomCoordinates(): Pair<Int, Int> = coordinateService.generateCoordinateSequence()
        .dropWhile { isGameObjectsContainsCoordinates(it.first, it.second) }
        .first()

    private fun isGameObjectsContainsCoordinates(x: Int, y: Int): Boolean =
        pointsOnScreen.any { it.x == x && it.y == y }
}
