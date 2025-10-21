package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.GameHandler
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.GameObject
import com.kiseru.asteroids.server.model.GameObject.Asteroid
import com.kiseru.asteroids.server.model.GameObject.Garbage
import com.kiseru.asteroids.server.model.GameObject.Spaceship
import com.kiseru.asteroids.server.model.GameStatus
import com.kiseru.asteroids.server.model.Player
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
            it !is Spaceship && it.x == spaceship.x && it.y == spaceship.y
        }

        lock.withLock {
            if (game.status != GameStatus.STARTED) {
                throw IllegalStateException("Game must have STARTED status")
            }

            onSpaceshipDamaged(game, player, spaceship)
            collisionPoint?.let { onGameObjectDamaged(game, it) }
            if (collisionPoint != null) {
                when (collisionPoint) {
                    is Asteroid -> subtractScore(player)

                    is Garbage -> {
                        addScore(player)
                        game.onGarbageCollected()
                    }

                    is Spaceship -> rollbackSpaceship(game, player, spaceship)

                }
            } else if (isOutOfField(spaceship)) {
                rollbackSpaceship(game, player, spaceship)
            }

            condition.signalAll()
        }
    }

    fun rollbackSpaceship(game: Game, player: Player, spaceship: Spaceship) {
        game.rollback(player.direction, spaceship)
        subtractScore(player)
    }

    private fun addScore(player: Player) {
        player.score += 10
    }

    private fun subtractScore(player: Player) {
        player.score -= 50
        player.status = if (player.score >= 0) Player.Status.Alive else Player.Status.Dead
    }

    private fun isOutOfField(spaceship: Spaceship): Boolean =
        spaceship.x == 0
                || spaceship.y == 0
                || spaceship.x > game.gameField.width
                || spaceship.y > game.gameField.height

    private fun onSpaceshipDamaged(game: Game, player: Player, spaceship: Spaceship) {
        if (player.status == Player.Status.Dead) {
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
