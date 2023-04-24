package com.kiseru.asteroids.server.command.impl

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.MessageSenderService

class IsGarbageCommandHandler : CommandHandler {

    override suspend fun handle(
        user: User,
        messageSenderService: MessageSenderService,
        closeSocket: suspend () -> Unit,
    ) {
        messageSenderService.send(user.spaceship?.isGarbageInFrontOf ?: false)
    }
}
