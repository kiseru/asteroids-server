package com.kiseru.asteroids.server.command.impl

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.UserService
import com.kiseru.asteroids.server.spaceship.SpaceshipService
import org.springframework.stereotype.Component
import java.util.*

@Component
class IsAsteroidCommandHandler(
    private val userService: UserService,
    private val spaceshipService: SpaceshipService,
) : CommandHandler {

    override suspend fun handle(
        userId: UUID,
        messageSenderService: MessageSenderService,
        closeSocket: suspend () -> Unit
    ) {
        val user = checkNotNull(userService.findUserById(userId))
        val spaceship = checkNotNull(spaceshipService.findSpaceshipById(user.spaceshipId))
        messageSenderService.send(spaceship.isAsteroidInFrontOf)
    }
}
