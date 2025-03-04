package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.GameHandler
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.GameStatus
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.Type
import com.kiseru.asteroids.server.service.GameService
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

class GameHandlerImpl(
    private val game: Game,
    private val lock: Lock,
    private val condition: Condition,
    private val gameService: GameService
) : GameHandler {

    override fun handle() {
        awaitStart()
        game.status = GameStatus.STARTED
        lock.withLock { condition.signalAll() }
        sendMessage("start")
        awaitFinish()
        sendMessage("finish")
        val rating = gameService.getGameRating(game)
        sendMessage(rating)
        println("Room released!")
        println(rating)
        println()
    }

    override fun awaitStart() {
        lock.withLock {
            while (game.getSpaceships().size < game.size) {
                condition.await()
            }
        }
    }

    fun checkSpaceship(spaceship: Spaceship) {
        val collisionPoint = game.gameObjects.firstOrNull {
            it.type != Type.SPACESHIP && it.isVisible && it.coordinates == spaceship.coordinates
        }

        if (collisionPoint != null) {
            lock.withLock {
                onSpaceshipDestroy(spaceship, collisionPoint.type)
                condition.signalAll()
            }
            collisionPoint.destroy()
            game.gameObjects.remove(collisionPoint)
        } else if (spaceship.x == 0
            || spaceship.y == 0
            || spaceship.x > game.screen.width
            || spaceship.y > game.screen.height
        ) {
            lock.withLock {
                onSpaceshipDestroy(spaceship, Type.WALL)
                condition.signalAll()
            }
        }
    }

    private fun onSpaceshipDestroy(spaceship: Spaceship, type: Type) {
        if (type == Type.ASTEROID) {
            if (game.status == GameStatus.STARTED) {
                spaceship.subtractScore()
            }
        } else if (type == Type.GARBAGE) {
            if (game.status == GameStatus.STARTED) {
                spaceship.addScore()
            }
            checkCollectedGarbage(game)
        } else if (type == Type.WALL) {
            // возвращаемся назад, чтобы не находится на стене
            rollbackLastStep(spaceship.direction, spaceship)
            if (game.status == GameStatus.STARTED) {
                spaceship.subtractScore()
            }
        }
        if (!spaceship.isAlive) {
            spaceship.destroy()
        }
    }

    private fun checkCollectedGarbage(game: Game) {
        val collected = game.incrementCollectedGarbageCount()
        if (collected >= game.garbageNumber) {
            game.status = GameStatus.FINISHED
        }
    }

    private fun rollbackLastStep(direction: Direction, spaceship: Spaceship) {
        spaceship.coordinates = when (direction) {
            Direction.UP -> spaceship.x to spaceship.y + 1
            Direction.RIGHT -> spaceship.x - 1 to spaceship.y
            Direction.DOWN -> spaceship.x to spaceship.y - 1
            Direction.LEFT -> spaceship.x + 1 to spaceship.y
        }
    }

    override fun awaitFinish() {
        lock.withLock {
            while (game.status != GameStatus.FINISHED) {
                condition.await()
            }
        }
    }

    override fun sendMessage(message: String) {
        for (handler in game.getSendMessageHandlers()) {
            handler(message)
        }
    }
}
