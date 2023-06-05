package com.kiseru.asteroids.server.command.factory

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.command.CommandType

interface CommandHandlerFactory {

    fun create(command: String): CommandHandler

    fun create(commandType: CommandType): CommandHandler
}
