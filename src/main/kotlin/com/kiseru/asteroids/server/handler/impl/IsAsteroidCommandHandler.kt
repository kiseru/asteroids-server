package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.handler.CommandHandler

class IsAsteroidCommandHandler : CommandHandler {

    override suspend fun handle(user: User) {
        user.sendMessage(if (user.isAsteroidInFrontOfSpaceship) "t" else "f")
    }
}