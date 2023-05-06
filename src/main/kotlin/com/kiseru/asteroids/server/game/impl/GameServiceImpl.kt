package com.kiseru.asteroids.server.game.impl

import com.kiseru.asteroids.server.factory.ScreenFactory
import com.kiseru.asteroids.server.game.GameService
import com.kiseru.asteroids.server.handler.impl.SpaceshipCrashHandlerImpl
import com.kiseru.asteroids.server.model.*
import com.kiseru.asteroids.server.properties.AsteroidsProperties
import com.kiseru.asteroids.server.service.impl.CourseCheckerServiceImpl
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Service
import java.util.*
import kotlin.random.Random

@Service
class GameServiceImpl(
    private val screenFactory: ScreenFactory,
    private val asteroidsProperties: AsteroidsProperties,
) : GameService {

    private val gameStorage = mutableMapOf<UUID, Game>()

    private val gameStorageMutex = Mutex()

    override fun registerSpaceshipForUser(game: Game, user: User, room: Room): Spaceship {
        val courseCheckerService = CourseCheckerServiceImpl(game.pointsOnScreen, game.screen)
        val (x, y) = generateUniqueRandomCoordinates(game)
        val spaceship = Spaceship(user, room, courseCheckerService, x, y)
        courseCheckerService.spaceship = spaceship
        game.pointsOnScreen.add(spaceship)
        game.gameObjects.add(spaceship)
        val spaceshipCrashHandler = SpaceshipCrashHandlerImpl(game, spaceship)
        game.crashHandlers.add(spaceshipCrashHandler)
        return spaceship
    }

    fun generateUniqueRandomCoordinates(game: Game): Pair<Int, Int> = generateCoordinateSequence()
        .dropWhile { isGameObjectsContainsCoordinates(game, it.first, it.second) }
        .first()

    private fun isGameObjectsContainsCoordinates(game: Game, x: Int, y: Int): Boolean =
        game.pointsOnScreen.any { it.x == x && it.y == y }

    override suspend fun createGame(): Game {
        val screen = screenFactory.createScreen()
        val pointsOnScreen = mutableListOf<Point>()
        val gameObjects = mutableListOf<Point>()
        generateAsteroids(pointsOnScreen, gameObjects)
        generateGarbage(pointsOnScreen, gameObjects)
        gameStorageMutex.withLock {
            val gameId = generateUniqueGameId()
            val game = Game(gameId, screen, asteroidsProperties.numberOfGarbageCells, pointsOnScreen, gameObjects)
            gameStorage[gameId] = game
            return game
        }
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

    private fun generateUniqueGameId() = generateSequence { UUID.randomUUID() }
        .dropWhile { gameStorage.containsKey(it) }
        .first()

    private fun generateUniqueRandomCoordinates(pointsOnScreen: MutableList<Point>): Pair<Int, Int> =
        generateCoordinateSequence()
            .dropWhile { isGameObjectsContainsCoordinates(it.first, it.second, pointsOnScreen) }
            .first()

    private fun generateCoordinateSequence(): Sequence<Pair<Int, Int>> =
        generateSequence { generateCoordinates(asteroidsProperties.screen.width, asteroidsProperties.screen.height) }

    private fun generateCoordinates(width: Int, height: Int): Pair<Int, Int> =
        Random.nextInt(width) + 1 to Random.nextInt(height) + 1

    private fun isGameObjectsContainsCoordinates(x: Int, y: Int, pointsOnScreen: MutableList<Point>): Boolean =
        pointsOnScreen.any { it.x == x && it.y == y }
}
