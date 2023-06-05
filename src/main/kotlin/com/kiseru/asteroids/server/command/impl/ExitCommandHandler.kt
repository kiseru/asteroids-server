package com.kiseru.asteroids.server.command.impl

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.service.MessageSenderService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.io.IOException
import java.util.*

@Component
class ExitCommandHandler : CommandHandler {

    override suspend fun handle(
        userId: UUID,
        messageSenderService: MessageSenderService,
        closeSocket: suspend () -> Unit
    ) {
        try {
            messageSenderService.sendExit()
            closeSocket()
        } catch (e: IOException) {
            log.error(e.localizedMessage, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.localizedMessage, e)
        }
    }

    companion object {

        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
