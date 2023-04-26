package com.kiseru.asteroids.server.command.direction

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.service.MessageSenderService

interface DirectionCommandHandler : CommandHandler {

    suspend fun handleDirection(
        user: User,
        room: Room,
        messageSenderService: MessageSenderService,
        direction: Direction,
        spaceship: Spaceship
    ) {
        spaceship.direction = direction
        room.refresh()
        messageSenderService.sendScore(user.score)
    }
}
