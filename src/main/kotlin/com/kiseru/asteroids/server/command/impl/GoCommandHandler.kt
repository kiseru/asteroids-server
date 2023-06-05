package com.kiseru.asteroids.server.command.impl

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.exception.UserNotFoundException
import com.kiseru.asteroids.server.model.ApplicationUser
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.RoomService
import com.kiseru.asteroids.server.service.UserService
import com.kiseru.asteroids.server.spaceship.SpaceshipService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
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
        moveSpaceship(user.spaceshipId)
        refreshRoom(user.roomId)
        messageSenderService.sendScore(user.score)
    }

    override suspend fun handle(user: ApplicationUser): String {
        moveSpaceship(user.spaceshipId)
        refreshRoom(user.roomId)
        if (user.score >= 0) {
            return user.score.toString()
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Game over")
        }
    }

    private fun moveSpaceship(spaceshipId: UUID) {
        val spaceship = checkNotNull(spaceshipService.findSpaceshipById(spaceshipId))
        spaceship.go()
    }

    private suspend fun refreshRoom(roomId: UUID) {
        val room = checkNotNull(roomService.findRoomById(roomId))
        room.refresh()
    }
}
