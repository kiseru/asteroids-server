package com.kiseru.asteroids.server.command.factory.impl

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.command.direction.impl.DownCommandHandler
import com.kiseru.asteroids.server.command.direction.impl.LeftCommandHandler
import com.kiseru.asteroids.server.command.direction.impl.RightCommandHandler
import com.kiseru.asteroids.server.command.direction.impl.UpCommandHandler
import com.kiseru.asteroids.server.command.factory.CommandHandlerFactory
import com.kiseru.asteroids.server.command.impl.*
import org.springframework.stereotype.Component

@Component
class CommandHandlerFactoryImpl : CommandHandlerFactory {

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
