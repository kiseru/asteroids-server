package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.User
import java.util.*

interface UserService {

    suspend fun createUser(username: String): User

    fun findUserById(userId: UUID): User?
}
