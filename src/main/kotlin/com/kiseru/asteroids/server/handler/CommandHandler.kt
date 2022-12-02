package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.model.User

interface CommandHandler {

    suspend fun handle(user: User)
}