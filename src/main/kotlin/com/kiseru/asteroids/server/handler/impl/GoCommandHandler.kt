package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.handler.CommandHandler

class GoCommandHandler : CommandHandler {

    override suspend fun handle(user: User) {
        user.moveSpaceship()
        user.refreshRoom()
        user.sendScore()
    }
}