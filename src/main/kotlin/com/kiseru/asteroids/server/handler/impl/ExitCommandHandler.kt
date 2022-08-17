package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.handler.CommandHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ExitCommandHandler : CommandHandler {

    override fun handle(user: User) = runBlocking {
        withContext(Dispatchers.IO) { user.closeConnection() }
    }
}