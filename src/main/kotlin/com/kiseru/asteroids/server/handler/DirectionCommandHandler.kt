package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.service.MessageSenderService

interface DirectionCommandHandler : CommandHandler {

    suspend fun handleDirection(user: User, messageSenderService: MessageSenderService, direction: Direction) {
        setSpaceshipDirection(user, direction)
        user.room.refresh()
        messageSenderService.sendScore(user.score)
    }

    private fun setSpaceshipDirection(user: User, direction: Direction) {
        checkNotNull(user.spaceship)
        user.spaceship?.direction = direction
    }
}