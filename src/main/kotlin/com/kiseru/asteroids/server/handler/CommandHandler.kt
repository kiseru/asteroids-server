package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.User

interface CommandHandler {

    fun handle(user: User)
}