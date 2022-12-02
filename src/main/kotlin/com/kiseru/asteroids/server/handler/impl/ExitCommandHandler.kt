package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.CommandHandler
import com.kiseru.asteroids.server.model.User

class ExitCommandHandler : CommandHandler {

    override suspend fun handle(user: User) = user.closeConnection()
}