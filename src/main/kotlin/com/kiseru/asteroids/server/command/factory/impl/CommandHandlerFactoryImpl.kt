package com.kiseru.asteroids.server.command.factory.impl

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.command.CommandType
import com.kiseru.asteroids.server.command.direction.impl.DownCommandHandler
import com.kiseru.asteroids.server.command.direction.impl.LeftCommandHandler
import com.kiseru.asteroids.server.command.direction.impl.RightCommandHandler
import com.kiseru.asteroids.server.command.direction.impl.UpCommandHandler
import com.kiseru.asteroids.server.command.factory.CommandHandlerFactory
import com.kiseru.asteroids.server.command.impl.ExitCommandHandler
import com.kiseru.asteroids.server.command.impl.GoCommandHandler
import com.kiseru.asteroids.server.command.impl.IsAsteroidCommandHandler
import com.kiseru.asteroids.server.command.impl.IsGarbageCommandHandler
import com.kiseru.asteroids.server.command.impl.IsWallCommandHandler
import com.kiseru.asteroids.server.command.impl.UnknownCommandHandler
import org.springframework.stereotype.Component

@Component
class CommandHandlerFactoryImpl(
    private val downCommandHandler: DownCommandHandler,
    private val leftCommandHandler: LeftCommandHandler,
    private val rightCommandHandler: RightCommandHandler,
    private val upCommandHandler: UpCommandHandler,
    private val exitCommandHandler: ExitCommandHandler,
    private val goCommandHandler: GoCommandHandler,
    private val isAsteroidCommandHandler: IsAsteroidCommandHandler,
    private val isGarbageCommandHandler: IsGarbageCommandHandler,
    private val isWallCommandHandler: IsWallCommandHandler,
    private val unknownCommandHandler: UnknownCommandHandler,
) : CommandHandlerFactory {

    override fun create(command: String): CommandHandler = when (command) {
        "go" -> goCommandHandler
        "left" -> leftCommandHandler
        "right" -> rightCommandHandler
        "up" -> upCommandHandler
        "down" -> downCommandHandler
        "isAsteroid" -> isAsteroidCommandHandler
        "isGarbage" -> isGarbageCommandHandler
        "isWall" -> isWallCommandHandler
        "exit" -> exitCommandHandler
        else -> unknownCommandHandler
    }

    override fun create(commandType: CommandType): CommandHandler =
        when (commandType) {
            CommandType.DOWN -> downCommandHandler
            CommandType.EXIT -> exitCommandHandler
            CommandType.GO -> goCommandHandler
            CommandType.IS_ASTEROID -> isAsteroidCommandHandler
            CommandType.IS_GARBAGE -> isGarbageCommandHandler
            CommandType.IS_WALL -> isWallCommandHandler
            CommandType.LEFT -> leftCommandHandler
            CommandType.RIGHT -> rightCommandHandler
            CommandType.UP -> upCommandHandler
        }
}
