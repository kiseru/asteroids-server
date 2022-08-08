package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.User
import java.net.Socket

interface UserService {

    suspend fun authorizeUser(socket: Socket): User
}