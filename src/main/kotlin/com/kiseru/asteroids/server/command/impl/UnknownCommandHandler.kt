package com.kiseru.asteroids.server.command.impl

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.service.MessageSenderService
import org.springframework.stereotype.Component
import java.util.*

@Component
class UnknownCommandHandler : CommandHandler {

    override suspend fun handle(
        userId: UUID,
        messageSenderService: MessageSenderService,
        closeSocket: suspend () -> Unit
    ) {
        messageSenderService.sendUnknownCommand()
    }
}
