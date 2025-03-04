package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.model.Game
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
        val mainMatrix = Array(game.fieldHeight + 2) { Array(game.fieldWidth + 2) { "." } }
        game.gameObjects.forEach {
            if (it.isVisible) {
                draw(mainMatrix, it.x, it.y, it.view())
            }
        }
        val stringBuilder = StringBuilder()
        for (i in 1 until game.fieldHeight + 1) {
            for (j in 1 until game.fieldWidth + 1) {
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
        game.getSpaceships()
            .sortedByDescending { it.score }
            .joinToString("\n") { "${it.user.username} ${it.score}" }
}
