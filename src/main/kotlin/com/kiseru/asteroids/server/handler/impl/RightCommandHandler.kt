package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.handler.DirectionCommandHandler
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.service.MessageSenderService

class RightCommandHandler : DirectionCommandHandler {

    override suspend fun handle(user: User, messageSenderService: MessageSenderService) =
        handleDirection(user, messageSenderService, Direction.RIGHT)
}