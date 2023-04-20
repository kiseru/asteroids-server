package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.User
import java.net.Socket

interface UserService {

    suspend fun authorizeUser(
        socket: Socket,
        room: Room,
        messageSenderService: MessageSenderService,
        username: String,
    ): User
}