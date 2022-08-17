package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.User

interface MessageSenderService {

    fun sendExit()

    fun sendScore(score: Int)

    fun sendSuccess()

    fun sendUnknownCommand()

    fun send(message: String)

    fun sendGameOver(score: Int)

    fun sendWelcomeMessage()

    fun sendInstructions(user: User)
}