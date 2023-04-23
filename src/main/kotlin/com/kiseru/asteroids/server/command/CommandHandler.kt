package com.kiseru.asteroids.server.command

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.MessageSenderService

interface CommandHandler {

    suspend fun handle(user: User, messageSenderService: MessageSenderService, closeSocket: suspend () -> Unit)
}
