package com.kiseru.asteroids.server.command.direction.impl

import com.kiseru.asteroids.server.command.direction.DirectionCommandHandler
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class DownCommandHandler(private val userService: UserService) : DirectionCommandHandler {

    override suspend fun handle(
        userId: UUID,
        room: Room,
        messageSenderService: MessageSenderService,
        spaceship: Spaceship,
        closeSocket: suspend () -> Unit
    ) {
        val user = userService.findUserById(userId)
        if (user == null) {
            log.warn("Failed to get user with id $userId")
            return
        }
        handleDirection(user, room, messageSenderService, Direction.DOWN, spaceship)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
