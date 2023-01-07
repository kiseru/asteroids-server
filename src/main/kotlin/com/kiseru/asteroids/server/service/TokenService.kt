package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.User

interface TokenService {

    fun generateToken(user: User): String
}