package com.kiseru.asteroids.server.command.direction.impl

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.command.direction.DirectionCommandHandler
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.service.MessageSenderService

class LeftCommandHandler : DirectionCommandHandler {

    override suspend fun handle(
        user: User,
        room: Room,
        messageSenderService: MessageSenderService,
        spaceship: Spaceship,
        closeSocket: suspend () -> Unit,
    ) = handleDirection(user, room, messageSenderService, Direction.LEFT, spaceship)
}
