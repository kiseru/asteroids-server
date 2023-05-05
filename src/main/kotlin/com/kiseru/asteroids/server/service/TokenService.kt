package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.User
import java.util.*

interface TokenService {

    fun generateToken(user: User): String

    fun getUserId(token: String): UUID
}
