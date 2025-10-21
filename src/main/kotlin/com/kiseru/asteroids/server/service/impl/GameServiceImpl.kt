package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.GameStatus
import com.kiseru.asteroids.server.model.Player
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.Type
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
            val gameField = display(game)
            outputStream.write("$gameField\n".toByteArray())
        } catch (_: IOException) {
            println("Failed to write the room's game field")
        }

    override fun writeGameField(game: Game, onMessageSend: (String) -> Unit): Unit =
        try {
            val gameField = display(game)
            onMessageSend("$gameField\n")
        } catch (_: IOException) {
            println("Failed to write the room's game field")
        }

    private fun display(game: Game): String {
        val mainMatrix = Array(game.gameField.height + 2) { Array(game.gameField.width + 2) { "." } }
        game.gameField.objects.forEach {
            draw(mainMatrix, it.x, it.y, it.view())
        }
        val stringBuilder = StringBuilder()
        for (i in 1 until game.gameField.height + 1) {
            for (j in 1 until game.gameField.width + 1) {
                stringBuilder.append(mainMatrix[i][j])
                stringBuilder.append("\t")
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }

    private fun draw(mainMatrix: Array<Array<String>>, x: Int, y: Int, symbol: String) {
        if (symbol.isBlank()) {
            mainMatrix[y][x] = "."
        }

        if (mainMatrix[y][x] == ".") {
            mainMatrix[y][x] = symbol
        } else {
            mainMatrix[y][x] = "${mainMatrix[y][x]}|$symbol"
        }
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

    override fun damageSpaceship(game: Game, player: Player, spaceship: Spaceship, type: Type) {
        if (game.status != GameStatus.STARTED) {
            throw IllegalStateException("Game must have STARTED status")
        }

        when (type) {
            Type.ASTEROID -> subtractScore(player)
            Type.GARBAGE -> {
                addScore(player)
                game.onGarbageCollected()
            }
            Type.WALL -> rollbackSpaceship(game, player, spaceship)
            Type.SPACESHIP -> rollbackSpaceship(game, player, spaceship)
        }
    }

    private fun rollbackSpaceship(game: Game, player: Player, spaceship: Spaceship) {
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

    override fun addGame(game: Game) {
        synchronized(games) {
            games.add(game)
        }
    }
}
