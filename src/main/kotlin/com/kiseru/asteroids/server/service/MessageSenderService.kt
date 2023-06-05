package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.ApplicationUser

interface MessageSenderService {

    suspend fun sendExit()

    suspend fun sendScore(score: Int)

    suspend fun sendUnknownCommand()

    suspend fun send(boolean: Boolean)

    suspend fun send(message: String)

    suspend fun sendGameOver(score: Int)

    suspend fun sendWelcomeMessage()

    suspend fun sendInstructions(user: ApplicationUser)
}