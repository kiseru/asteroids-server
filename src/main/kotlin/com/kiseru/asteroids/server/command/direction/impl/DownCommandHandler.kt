package com.kiseru.asteroids.server.command.direction.impl

import com.kiseru.asteroids.server.command.direction.DirectionCommandHandler
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.RoomService
import com.kiseru.asteroids.server.service.UserService
import com.kiseru.asteroids.server.spaceship.SpaceshipService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class DownCommandHandler(
    private val roomService: RoomService,
    private val userService: UserService,
    private val spaceshipService: SpaceshipService,
) : DirectionCommandHandler {

    override suspend fun handle(
        userId: UUID,
        messageSenderService: MessageSenderService,
        closeSocket: suspend () -> Unit
    ) {
        val user = userService.findUserById(userId)
        if (user == null) {
            log.warn("Failed to get user with id $userId")
            return
        }

        val room = checkNotNull(roomService.findRoomById(user.roomId))
        val spaceship = checkNotNull(spaceshipService.findSpaceshipById(user.spaceshipId))
        handleDirection(user, room, messageSenderService, Direction.DOWN, spaceship)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
