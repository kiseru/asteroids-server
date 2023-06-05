package com.kiseru.asteroids.server.game.impl

import com.kiseru.asteroids.server.coordinate.CoordinateService
import com.kiseru.asteroids.server.factory.ScreenFactory
import com.kiseru.asteroids.server.game.GameService
import com.kiseru.asteroids.server.handler.impl.SpaceshipCrashHandlerImpl
import com.kiseru.asteroids.server.model.Asteroid
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Garbage
import com.kiseru.asteroids.server.model.Point
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.properties.AsteroidsProperties
import com.kiseru.asteroids.server.spaceship.SpaceshipService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameServiceImpl(
    private val coordinateService: CoordinateService,
    private val screenFactory: ScreenFactory,
    private val asteroidsProperties: AsteroidsProperties,
    private val spaceshipService: SpaceshipService,
) : GameService {

    private val gameStorage = mutableMapOf<UUID, Game>()

    private val gameStorageMutex = Mutex()

    override suspend fun registerSpaceshipForUser(game: Game, user: User, room: Room): Spaceship {
        val spaceship = spaceshipService.createSpaceship(user, room, game)
        game.pointsOnScreen.add(spaceship)
        game.gameObjects.add(spaceship)
        val spaceshipCrashHandler = SpaceshipCrashHandlerImpl(game, spaceship)
        game.crashHandlers.add(spaceshipCrashHandler)
        return spaceship
    }

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
            val (x, y) = coordinateService.generateUniqueRandomCoordinates(pointsOnScreen)
            val asteroid = Asteroid(x, y)
            pointsOnScreen.add(asteroid)
            gameObjects.add(asteroid)
        }
    }

    private fun generateGarbage(pointsOnScreen: MutableList<Point>, gameObjects: MutableList<Point>) {
        for (i in 0 until asteroidsProperties.numberOfGarbageCells) {
            val (x, y) = coordinateService.generateUniqueRandomCoordinates(pointsOnScreen)
            val garbage = Garbage(x, y)
            pointsOnScreen.add(garbage)
            gameObjects.add(garbage)
        }
    }

    private fun generateUniqueGameId() = generateSequence { UUID.randomUUID() }
        .dropWhile { gameStorage.containsKey(it) }
        .first()
}
