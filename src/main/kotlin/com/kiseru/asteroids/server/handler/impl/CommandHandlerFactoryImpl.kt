package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.CommandHandler
import com.kiseru.asteroids.server.handler.CommandHandlerFactory

object CommandHandlerFactoryImpl : CommandHandlerFactory {

    override fun create(command: String): CommandHandler = when (command) {
        "go" -> GoCommandHandler()
        "left" -> LeftCommandHandler()
        "right" -> RightCommandHandler()
        "up" -> UpCommandHandler()
        "down" -> DownCommandHandler()
        "isAsteroid" -> IsAsteroidCommandHandler()
        "isGarbage" -> IsGarbageCommandHandler()
        "isWall" -> IsWallCommandHandler()
        "exit" -> ExitCommandHandler()
        else -> UnknownCommandHandler()
    }
}

