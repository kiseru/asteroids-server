package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.CommandHandler
import com.kiseru.asteroids.server.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExitCommandHandler : CommandHandler {

    override suspend fun handle(user: User) = withContext(Dispatchers.IO) { user.closeConnection() }
}