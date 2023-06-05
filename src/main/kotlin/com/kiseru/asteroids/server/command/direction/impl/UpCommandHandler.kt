package com.kiseru.asteroids.server.command.direction.impl

import com.kiseru.asteroids.server.command.direction.DirectionCommandHandler
import com.kiseru.asteroids.server.exception.UserNotFoundException
import com.kiseru.asteroids.server.model.ApplicationUser
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.RoomService
import com.kiseru.asteroids.server.service.UserService
import com.kiseru.asteroids.server.spaceship.SpaceshipService
import org.springframework.stereotype.Component
import java.util.*

@Component
class UpCommandHandler(
    private val roomService: RoomService,
    private val userService: UserService,
    private val spaceshipService: SpaceshipService,
) : DirectionCommandHandler {

    override suspend fun handle(
        userId: UUID,
        messageSenderService: MessageSenderService,
        closeSocket: suspend () -> Unit
    ) {
        val user = userService.findUserById(userId) ?: throw UserNotFoundException(userId)
        val room = checkNotNull(roomService.findRoomById(user.roomId))
        val spaceship = checkNotNull(spaceshipService.findSpaceshipById(user.spaceshipId))
        handleDirection(user, room, messageSenderService, Direction.LEFT, spaceship)
    }

    override suspend fun handle(user: ApplicationUser): String {
        val room = checkNotNull(roomService.findRoomById(user.roomId))
        val spaceship = checkNotNull(spaceshipService.findSpaceshipById(user.spaceshipId))
        handleDirection(Direction.UP, user, room, spaceship)
        return user.score.toString()
    }
}
