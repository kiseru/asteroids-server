package com.kiseru.asteroids.server.command.direction

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.model.ApplicationUser
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.service.MessageSenderService

interface DirectionCommandHandler : CommandHandler {

    suspend fun handleDirection(
        user: ApplicationUser,
        room: Room,
        messageSenderService: MessageSenderService,
        direction: Direction,
        spaceship: Spaceship
    ) {
        spaceship.direction = direction
        room.refresh()
        messageSenderService.sendScore(user.score)
    }

    suspend fun handleDirection(
        direction: Direction,
        user: ApplicationUser,
        room: Room,
        spaceship: Spaceship,
    ): String {
        spaceship.direction = direction
        room.refresh()
        return user.score.toString()
    }
}
