package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.ApplicationUser
import com.kiseru.asteroids.server.model.Room
import java.util.*

interface UserService {

    suspend fun createUser(username: String, room: Room): ApplicationUser

    fun findUserById(userId: UUID): ApplicationUser?

    fun findUserByUsername(username: String): ApplicationUser?
}
