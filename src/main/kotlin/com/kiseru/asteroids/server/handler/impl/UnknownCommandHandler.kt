package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.handler.CommandHandler

class UnknownCommandHandler : CommandHandler {

    override suspend fun handle(user: User) {
        user.messageSenderService.sendUnknownCommand()
    }
}