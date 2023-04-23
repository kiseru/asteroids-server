package com.kiseru.asteroids.server.command.direction.impl

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.command.direction.DirectionCommandHandler
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.service.MessageSenderService

class RightCommandHandler : DirectionCommandHandler {

    override suspend fun handle(
        user: User,
        messageSenderService: MessageSenderService,
        closeSocket: suspend () -> Unit,
    ) = handleDirection(user, messageSenderService, Direction.RIGHT)
}
