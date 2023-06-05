package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.ApplicationUser
import java.util.*

interface TokenService {

    fun generateToken(user: ApplicationUser): String

    fun getUserId(token: String): UUID
}
