package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.CommandHandler
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.MessageSenderService

class GoCommandHandler : CommandHandler {

    override suspend fun handle(
        user: User,
        messageSenderService: MessageSenderService,
        closeSocket: suspend () -> Unit,
    ) {
        moveSpaceship(user)
        user.room.refresh()
        messageSenderService.sendScore(user.score)
    }

    private fun moveSpaceship(user: User) {
        checkNotNull(user.spaceship)
        user.spaceship?.go()
    }
}
