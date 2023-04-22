package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.handler.CommandHandler
import com.kiseru.asteroids.server.service.MessageSenderService

class UnknownCommandHandler : CommandHandler {

    override suspend fun handle(user: User, messageSenderService: MessageSenderService) {
        messageSenderService.sendUnknownCommand()
    }
}