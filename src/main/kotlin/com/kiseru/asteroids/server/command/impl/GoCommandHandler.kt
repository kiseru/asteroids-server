package com.kiseru.asteroids.server.command.impl

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.exception.UserNotFoundException
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.UserService
import org.springframework.stereotype.Component
import java.util.*

@Component
class GoCommandHandler(private val userService: UserService) : CommandHandler {

    override suspend fun handle(
        userId: UUID,
        room: Room,
        messageSenderService: MessageSenderService,
        spaceship: Spaceship,
        closeSocket: suspend () -> Unit
    ) {
        val user = userService.findUserById(userId) ?: throw UserNotFoundException(userId)
        spaceship.go()
        room.refresh()
        messageSenderService.sendScore(user.score)
    }
}
