package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.User

interface MessageSenderService {

    suspend fun sendExit()

    fun sendScore(score: Int)

    fun sendUnknownCommand()

    fun send(message: String)

    fun sendGameOver(score: Int)

    fun sendWelcomeMessage()

    suspend fun sendInstructions(user: User)
}