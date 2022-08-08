package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.model.Room
import java.net.Socket

interface UserService {

    suspend fun authorizeUser(socket: Socket, room: Room): User
}