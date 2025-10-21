package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.GameHandler
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.GameObject
import com.kiseru.asteroids.server.model.GameStatus
import com.kiseru.asteroids.server.model.Player
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
    private val gameService: GameService,
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
            while (game.getPlayers().size < game.spaceshipCapacity) {
                condition.await()
            }
        }
    }

    fun checkSpaceship(player: Player, spaceship: Spaceship) {
        val collisionPoint = game.gameField.objects.firstOrNull {
            it.type != Type.SPACESHIP && it.x == spaceship.x && it.y == spaceship.y
        }

        val collisionPointType = getCollisionPointType(spaceship, collisionPoint)
        lock.withLock {
            onSpaceshipDamaged(game, player, spaceship)
            collisionPoint?.let { onGameObjectDamaged(game, it) }
            collisionPointType?.let { gameService.damageSpaceship(game, player, spaceship, it) }
            condition.signalAll()
        }
    }

    private fun getCollisionPointType(spaceship: Spaceship, collisionGameObject: GameObject?): Type? =
        collisionGameObject?.type
            ?: if (isOutOfField(spaceship)) Type.WALL else null

    private fun isOutOfField(spaceship: Spaceship): Boolean =
        spaceship.x == 0
                || spaceship.y == 0
                || spaceship.x > game.gameField.width
                || spaceship.y > game.gameField.height

    private fun onSpaceshipDamaged(game: Game, player: Player, spaceship: Spaceship) {
        if (!player.isAlive) {
            onGameObjectDamaged(game, spaceship)
        }
    }

    private fun onGameObjectDamaged(game: Game, gameObject: GameObject) {
        game.removeGameObject(gameObject)
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
