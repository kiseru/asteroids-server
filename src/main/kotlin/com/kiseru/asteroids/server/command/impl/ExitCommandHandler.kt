package com.kiseru.asteroids.server.command.impl

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.MessageSenderService
import java.io.IOException
import java.lang.RuntimeException

class ExitCommandHandler : CommandHandler {

    override suspend fun handle(
        user: User,
        messageSenderService: MessageSenderService,
        spaceship: Spaceship,
        closeSocket: suspend () -> Unit,
    ) {
        try {
            messageSenderService.sendExit()
            closeSocket()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
