package com.kiseru.asteroids.server.command

import com.kiseru.asteroids.server.model.ApplicationUser
import com.kiseru.asteroids.server.service.MessageSenderService
import java.util.*

interface CommandHandler {

    suspend fun handle(
        userId: UUID,
        messageSenderService: MessageSenderService,
        closeSocket: suspend () -> Unit,
    )

    suspend fun handle(user: ApplicationUser): String
}
