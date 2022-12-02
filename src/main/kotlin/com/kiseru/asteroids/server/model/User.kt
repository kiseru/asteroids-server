package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.exception.GameFinishedException
import com.kiseru.asteroids.server.handler.CommandHandlerFactory
import com.kiseru.asteroids.server.service.MessageReceiverService
import com.kiseru.asteroids.server.service.MessageSenderService
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.Socket

class User(
    val username: String,
    val room: Room,
    private val socket: Socket,
    private val messageReceiverService: MessageReceiverService,
    private val messageSenderService: MessageSenderService,
    private val commandHandlerFactory: CommandHandlerFactory,
) {

    val id = nextId++

    val isAsteroidInFrontOfSpaceship
        get() = spaceship?.isAsteroidInFrontOf ?: false

    val isGarbageInFrontOfSpaceship
        get() = spaceship?.isGarbageInFrontOf ?: false

    val isWallInFrontOfSpaceship
        get() = spaceship?.isWallInFrontOf ?: false

    var score = 100
        private set

    var isAlive = true
        private set

    var spaceship: Spaceship? = null

    private var steps = 0

    suspend fun awaitCreatingSpaceship() {
        room.awaitCreatingSpaceship(this)
    }

    suspend fun run() = coroutineScope {
        awaitCreatingSpaceship()
        try {
            while (!room.isGameFinished && isAlive) {
                val command = messageReceiverService.receive()
                handleCommand(command)
                incrementSteps()
                checkIsAlive()
            }
        } finally {
            isAlive = false
            room.setGameFinished()
        }
    }

    fun sendMessage(message: String) {
        messageSenderService.send(message)
    }

    fun addScore() {
        if (room.isGameFinished) {
            throw GameFinishedException()
        }

        score += 10
    }

    fun subtractScore() {
        if (room.isGameFinished) {
            throw GameFinishedException()
        }

        score -= 50
        if (score < 0) {
            isAlive = false
        }
    }

    fun died() {
        isAlive = false
        messageSenderService.sendGameOver(score)
    }

    fun checkCollectedGarbage(collected: Int) {
        room.checkCollectedGarbage(collected)
    }

    fun moveSpaceship() {
        checkNotNull(spaceship)
        spaceship?.go()
    }

    fun refreshRoom() {
        room.refresh()
    }

    fun sendScore() {
        messageSenderService.sendScore(score)
    }

    fun setSpaceshipDirection(direction: Direction) {
        checkNotNull(spaceship)
        spaceship?.direction = direction
    }

    fun hasSpaceship(): Boolean = spaceship != null

    fun closeConnection() {
        try {
            log.info("Closing connection with $username")
            messageSenderService.sendExit()
            socket.close()
            log.info("Connection with $username has been closed")
        } catch (e: IOException) {
            log.error("Failed to close connection", e)
        }
    }

    fun sendUnknownCommandMessage() {
        messageSenderService.sendUnknownCommand()
    }

    private suspend fun handleCommand(command: String) {
        val commandHandler = commandHandlerFactory.create(command)
        commandHandler.handle(this)
    }

    private fun incrementSteps() {
        if (!isAlive) {
            throw GameFinishedException()
        }

        steps++
    }

    private fun checkIsAlive() {
        if (steps >= 1500 || score < 0) {
            died()
        }
    }

    companion object {

        private val log = LoggerFactory.getLogger(User::class.java)

        private var nextId = 0
    }
}