package com.kiseru.asteroids.server.command.impl

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.exception.UserNotFoundException
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.RoomService
import com.kiseru.asteroids.server.service.UserService
import com.kiseru.asteroids.server.spaceship.SpaceshipService
import org.springframework.stereotype.Component
import java.util.*

@Component
class GoCommandHandler(
    private val roomService: RoomService,
    private val userService: UserService,
    private val spaceshipService: SpaceshipService,
) : CommandHandler {

    override suspend fun handle(
        userId: UUID,
        messageSenderService: MessageSenderService,
        closeSocket: suspend () -> Unit
    ) {
        val user = userService.findUserById(userId) ?: throw UserNotFoundException(userId)

        val spaceship = checkNotNull(spaceshipService.findSpaceshipById(user.spaceshipId))
        spaceship.go()

        val room = checkNotNull(roomService.findRoomById(user.roomId))
        room.refresh()

        messageSenderService.sendScore(user.score)
    }
}
