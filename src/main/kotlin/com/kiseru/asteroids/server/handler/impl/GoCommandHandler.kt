package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.handler.CommandHandler

class GoCommandHandler : CommandHandler {

    override fun handle(user: User) {
        user.moveSpaceship()
        user.refreshRoom()
        user.sendScore()
    }
}