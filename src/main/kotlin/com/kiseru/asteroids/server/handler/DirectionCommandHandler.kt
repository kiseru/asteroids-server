package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.model.Direction

interface DirectionCommandHandler : CommandHandler {

    suspend fun handleDirection(user: User, direction: Direction) {
        user.setSpaceshipDirection(direction)
        user.refreshRoom()
        user.sendScore()
    }
}