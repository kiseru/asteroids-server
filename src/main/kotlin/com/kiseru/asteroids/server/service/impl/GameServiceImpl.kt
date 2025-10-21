package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.GameField
import com.kiseru.asteroids.server.model.GameObject
import com.kiseru.asteroids.server.model.GameObject.Asteroid
import com.kiseru.asteroids.server.model.GameObject.Garbage
import com.kiseru.asteroids.server.model.GameObject.Spaceship
import com.kiseru.asteroids.server.model.GameStatus
import com.kiseru.asteroids.server.service.GameService
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

class GameServiceImpl : GameService {

    private val games = mutableListOf<Game>()

    override fun writeRatings(outputStream: OutputStream): Unit =
        synchronized(this) {
            for (game in games) {
                writeRating(game, outputStream)
            }
        }

    private fun writeRating(game: Game, outputStream: OutputStream): Unit =
        try {
            val rating = getGameRating(game)
            outputStream.write("$rating\n".toByteArray())
        } catch (_: IOException) {
            println("Failed to write the room's rating")
        }

    override fun writeGameFields(outputStream: OutputStream): Unit =
        synchronized(this) {
            for (game in games) {
                writeGameField(game, outputStream)
            }
        }

    override fun writeGameField(game: Game, outputStream: OutputStream): Unit =
        try {
            val gameField = display(game.gameField)
            outputStream.write("$gameField\n".toByteArray())
        } catch (_: IOException) {
            println("Failed to write the room's game field")
        }

    override fun writeGameField(game: Game, onMessageSend: (String) -> Unit): Unit =
        try {
            val gameField = display(game.gameField)
            onMessageSend("$gameField\n")
        } catch (_: IOException) {
            println("Failed to write the room's game field")
        }

    private fun display(gameField: GameField): String {
        val stringBuilder = StringBuilder()
        for (i in 1 until gameField.height + 1) {
            for (j in 1 until gameField.width + 1) {
                val gameObject = gameField.objects.firstOrNull { it.x == j && it.y == i }
                val symbol = if (gameObject != null) view(gameObject) else "."
                val paddedSymbol = if (j == 1) symbol else symbol.padStart(3)
                stringBuilder.append(paddedSymbol)
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }

    private fun view(gameObject: GameObject): String =
        when (gameObject) {
            is Asteroid -> "A"
            is Garbage -> "G"
            is Spaceship -> gameObject.user.id.toString()
        }

    override fun createGameHandler(lock: Lock, condition: Condition): (Game) -> Unit =
        { handleGame(lock, condition, it) }

    private fun handleGame(lock: Lock, condition: Condition, game: Game) {
        lock.withLock {
            while (game.status != GameStatus.FINISHED) {
                condition.await()
            }
        }

        for (handler in game.getSendMessageHandlers()) {
            handler("finish")
        }

        val rating = getGameRating(game)
        for (handler in game.getSendMessageHandlers()) {
            handler(rating)
        }

        println("Room released!")
        println(rating)
        println()
    }

    override fun getGameRating(game: Game): String =
        game.getPlayers()
            .sortedByDescending { (player, _) -> player.score }
            .joinToString("\n") { (player, spaceship) -> "${spaceship.user.username} ${player.score}" }

    override fun addGame(game: Game) {
        synchronized(games) {
            games.add(game)
        }
    }
}
