package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.model.Direction

interface DirectionCommandHandler : CommandHandler {

    suspend fun handleDirection(user: User, direction: Direction) {
        setSpaceshipDirection(user, direction)
        user.refreshRoom()
        user.sendScore()
    }

    private fun setSpaceshipDirection(user: User, direction: Direction) {
        checkNotNull(user.spaceship)
        user.spaceship?.direction = direction
    }
}