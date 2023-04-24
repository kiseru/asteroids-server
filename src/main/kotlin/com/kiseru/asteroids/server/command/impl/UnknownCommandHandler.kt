package com.kiseru.asteroids.server.command.impl

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.MessageSenderService

class UnknownCommandHandler : CommandHandler {

    override suspend fun handle(
        user: User,
        messageSenderService: MessageSenderService,
        spaceship: Spaceship,
        closeSocket: suspend () -> Unit,
    ) {
        messageSenderService.sendUnknownCommand()
    }
}
