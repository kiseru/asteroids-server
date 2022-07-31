package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.handler.CommandHandler

class IsGarbageCommandHandler : CommandHandler {

    override fun handle(user: User) {
        user.sendMessage(if (user.isGarbageInFrontOfSpaceship) "t" else "f")
    }
}