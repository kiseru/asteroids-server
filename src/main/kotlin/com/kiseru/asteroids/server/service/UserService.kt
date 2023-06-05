package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.User
import java.util.*

interface UserService {

    suspend fun createUser(username: String, room: Room): User

    fun findUserById(userId: UUID): User?
}
