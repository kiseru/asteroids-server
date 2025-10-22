package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.GameObject
import com.kiseru.asteroids.server.model.GameStatus
import com.kiseru.asteroids.server.model.Player
import com.kiseru.asteroids.server.model.User
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class GameHandler(
    private val connectionHandler: ConnectionHandler,
    private val user: User,
    private val game: Game,
    private val spaceship: GameObject.Spaceship,
) {

    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    private var gameStatus = GameStatus.STARTED

    fun handle() {
        val player = Player()
        synchronized(game) {
            game.addGameObject(spaceship)
            game.addSpaceship(player, spaceship, connectionHandler::sendMessage)
        }
        connectionHandler.sendMessage("You joined the room \"$game.name\"")
        try {
            sendInstructions()
            connectionHandler.sendMessage("start")
            val commandHandler = CommandHandler(connectionHandler, game, player, spaceship)
            while (gameStatus != GameStatus.FINISHED && player.status == Player.Status.Alive) {
                val command = connectionHandler.receiveCommand()
                synchronized(game) {
                    if (game.hasGarbage()) commandHandler.handleCommand(command) else gameStatus = GameStatus.FINISHED
                }
            }
            connectionHandler.sendMessage("finish")
            sendRating()
        } catch (_: IOException) {
            println("Connection problems with user " + user.username)
        } finally {
            player.status = Player.Status.Dead
            game.removeGameObject(spaceship)
            lock.withLock {
                val aliveUsersCount = game.getPlayers().count { (player, _) -> player.status == Player.Status.Alive }
                if (aliveUsersCount == 0) {
                    gameStatus = GameStatus.FINISHED
                }
                condition.signalAll()
            }
        }
    }

    private fun sendInstructions() {
        connectionHandler.sendMessage("You need to keep a space garbage.")
        connectionHandler.sendMessage("Your ID is ${spaceship.id}")
        connectionHandler.sendMessage("Good luck, Commander!")
    }

    private fun sendRating() {
        val rating = game.getPlayers()
            .sortedByDescending { (player, _) -> player.score }
            .joinToString("\n") { (player, spaceship) -> "${spaceship.id} ${player.score}" }
        connectionHandler.sendMessage(rating)
    }
}