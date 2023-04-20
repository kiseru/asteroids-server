package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.handler.CommandHandler

class GoCommandHandler : CommandHandler {

    override suspend fun handle(user: User) {
        moveSpaceship(user)
        user.refreshRoom()
        user.sendScore()
    }

    private fun moveSpaceship(user: User) {
        checkNotNull(user.spaceship)
        user.spaceship?.go()
    }
}