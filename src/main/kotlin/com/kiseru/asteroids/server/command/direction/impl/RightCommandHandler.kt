package com.kiseru.asteroids.server.command.direction.impl

import com.kiseru.asteroids.server.command.direction.DirectionCommandHandler
import com.kiseru.asteroids.server.exception.UserNotFoundException
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.UserService
import org.springframework.stereotype.Component
import java.util.*

@Component
class RightCommandHandler(private val userService: UserService) : DirectionCommandHandler {

    override suspend fun handle(
        userId: UUID,
        room: Room,
        messageSenderService: MessageSenderService,
        spaceship: Spaceship,
        closeSocket: suspend () -> Unit
    ) {
        val user = userService.findUserById(userId) ?: throw UserNotFoundException(userId)
        handleDirection(user, room, messageSenderService, Direction.LEFT, spaceship)
    }
}
