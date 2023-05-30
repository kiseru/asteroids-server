package com.kiseru.asteroids.server.command

import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.service.MessageSenderService
import java.util.*

interface CommandHandler {

    suspend fun handle(
        userId: UUID,
        room: Room,
        messageSenderService: MessageSenderService,
        spaceship: Spaceship,
        closeSocket: suspend () -> Unit,
    )
}
