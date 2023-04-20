package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.exception.GameFinishedException
import com.kiseru.asteroids.server.service.MessageReceiverService
import com.kiseru.asteroids.server.service.MessageSenderService
import java.net.Socket

class User(
    val id: String,
    val username: String,
    val room: Room,
    val socket: Socket,
    val messageReceiverService: MessageReceiverService,
    val messageSenderService: MessageSenderService,
) {

    val isAsteroidInFrontOfSpaceship
        get() = spaceship?.isAsteroidInFrontOf ?: false

    val isGarbageInFrontOfSpaceship
        get() = spaceship?.isGarbageInFrontOf ?: false

    val isWallInFrontOfSpaceship
        get() = spaceship?.isWallInFrontOf ?: false

    var score = 100
        private set

    var isAlive = true

    var spaceship: Spaceship? = null

    var steps = 0

    suspend fun sendMessage(message: String) {
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

    fun refreshRoom() {
        room.refresh()
    }

    suspend fun sendScore() {
        messageSenderService.sendScore(score)
    }
}