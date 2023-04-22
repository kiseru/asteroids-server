package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.User

interface UserService {

    suspend fun authorizeUser(room: Room, messageSenderService: MessageSenderService, username: String): User
}
