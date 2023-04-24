package com.kiseru.asteroids.server.command.factory

import com.kiseru.asteroids.server.command.CommandHandler

interface CommandHandlerFactory {

    fun create(command: String): CommandHandler
}
