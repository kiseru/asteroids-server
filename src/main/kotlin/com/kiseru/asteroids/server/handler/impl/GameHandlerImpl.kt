package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.GameHandler
import com.kiseru.asteroids.server.messaging.GameMessageBroadcaster
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.GameObject
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.Type
import com.kiseru.asteroids.server.service.GameService
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

class GameHandlerImpl(
    private val game: Game,
    private val gameMessageBroadcaster: GameMessageBroadcaster,
    private val lock: Lock,
    private val condition: Condition,
    private val gameService: GameService,
) : GameHandler {

    override fun handle() {
        awaitStart()
        game.start()
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
        val collisionPoint = game.findCollidableObjectAt(spaceship.position)
        val collisionPointType = getCollisionPointType(spaceship, collisionPoint)
        lock.withLock {
            game.destroySpaceship(spaceship)
            collisionPoint?.let { game.removeGameObject(it) }
            collisionPointType?.let { game.damageSpaceship(spaceship, it) }
            condition.signalAll()
        }
    }

    private fun getCollisionPointType(spaceship: Spaceship, collisionGameObject: GameObject?): Type? =
        collisionGameObject?.type
            ?: if (isOutOfField(spaceship)) Type.WALL else null

    private fun isOutOfField(spaceship: Spaceship): Boolean =
        !game.fieldSize.contains(spaceship.position)

    override fun awaitFinish() {
        lock.withLock {
            while (game.status != Game.Status.FINISHED) {
                condition.await()
            }
        }
    }

    override fun sendMessage(message: String) {
        gameMessageBroadcaster.broadcast(message)
    }
}
