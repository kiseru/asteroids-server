package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.User

interface UserService {

    suspend fun authorizeUser(messageSenderService: MessageSenderService, username: String): User
}
