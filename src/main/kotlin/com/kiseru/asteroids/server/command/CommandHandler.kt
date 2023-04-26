package com.kiseru.asteroids.server.command

import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.MessageSenderService

interface CommandHandler {

    suspend fun handle(
        user: User,
        room: Room,
        messageSenderService: MessageSenderService,
        spaceship: Spaceship,
        closeSocket: suspend () -> Unit,
    )
}
