package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.CommandHandler
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.MessageSenderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

class ExitCommandHandler : CommandHandler {

    override suspend fun handle(
        user: User,
        messageSenderService: MessageSenderService,
        closeSocket: suspend () -> Unit,
    ) {
        try {
            messageSenderService.sendExit()
            closeSocket()
        } catch (e: IOException) {
            log.error(e.localizedMessage, e)
        }
    }

    companion object {

        private val log: Logger = LoggerFactory.getLogger(ExitCommandHandler::class.java)
    }
}
