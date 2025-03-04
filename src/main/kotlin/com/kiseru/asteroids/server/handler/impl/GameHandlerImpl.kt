package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.GameHandler
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
                game.damageSpaceship(spaceship, collisionPoint.type)
                condition.signalAll()
            }
            collisionPoint.destroy()
            game.gameObjects.remove(collisionPoint)
        } else if (spaceship.x == 0
            || spaceship.y == 0
            || spaceship.x > game.fieldWidth
            || spaceship.y > game.fieldHeight
        ) {
            lock.withLock {
                game.damageSpaceship(spaceship, Type.WALL)
                condition.signalAll()
            }
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
