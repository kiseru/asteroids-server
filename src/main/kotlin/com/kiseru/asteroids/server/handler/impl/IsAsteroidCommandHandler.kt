package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.handler.CommandHandler

class IsAsteroidCommandHandler : CommandHandler {

    override fun handle(user: User) {
        user.sendMessage(if (user.isAsteroidInFrontOfSpaceship) "t" else "f")
    }
}