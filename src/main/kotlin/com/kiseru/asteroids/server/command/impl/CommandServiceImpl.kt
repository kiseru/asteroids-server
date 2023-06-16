package com.kiseru.asteroids.server.command.impl

import com.kiseru.asteroids.server.command.CommandService
import com.kiseru.asteroids.server.command.CommandType
import com.kiseru.asteroids.server.command.dto.CommandResponseDto
import com.kiseru.asteroids.server.command.factory.CommandHandlerFactory
import com.kiseru.asteroids.server.service.UserService
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service

@Service
class CommandServiceImpl(
    private val commandHandlerFactory: CommandHandlerFactory,
    private val userService: UserService,
) : CommandService {

    override suspend fun handleDownCommand(user: User): CommandResponseDto =
        handleCommand(CommandType.DOWN, user)

    override suspend fun handleExitCommand(user: User): CommandResponseDto =
        handleCommand(CommandType.EXIT, user)

    override suspend fun handleGoCommand(user: User): CommandResponseDto =
        handleCommand(CommandType.GO, user)

    override suspend fun handleIsAsteroidCommand(user: User): CommandResponseDto =
        handleCommand(CommandType.IS_ASTEROID, user)

    override suspend fun handleIsGarbageCommand(user: User): CommandResponseDto =
        handleCommand(CommandType.IS_GARBAGE, user)

    override suspend fun handleIsWallCommand(user: User): CommandResponseDto =
        handleCommand(CommandType.IS_WALL, user)

    override suspend fun handleLeftCommand(user: User): CommandResponseDto =
        handleCommand(CommandType.LEFT, user)

    override suspend fun handleRightCommand(user: User): CommandResponseDto =
        handleCommand(CommandType.RIGHT, user)

    override suspend fun handleUpCommand(user: User): CommandResponseDto =
        handleCommand(CommandType.UP, user)

    private suspend fun handleCommand(commandType: CommandType, user: User): CommandResponseDto {
        val applicationUser = checkNotNull(userService.findUserByUsername(user.username))
        val commandHandler = commandHandlerFactory.create(commandType)
        val result = commandHandler.handle(applicationUser)
        return CommandResponseDto(result)
    }
}
