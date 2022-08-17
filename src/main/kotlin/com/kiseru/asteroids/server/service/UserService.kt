package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.User
import java.net.Socket

interface UserService {

    suspend fun authorizeUser(socket: Socket): User
}