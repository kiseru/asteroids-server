package com.kiseru.asteroids.server.command.impl

import com.kiseru.asteroids.server.command.dto.CommandRequestDto
import com.kiseru.asteroids.server.command.dto.CommandResponseDto
import com.kiseru.asteroids.server.command.CommandService
import com.kiseru.asteroids.server.command.factory.CommandHandlerFactory
import com.kiseru.asteroids.server.service.UserService
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service

@Service
class CommandServiceImpl(
    private val commandHandlerFactory: CommandHandlerFactory,
    private val userService: UserService,
) : CommandService {

    override suspend fun handleCommand(requestBody: CommandRequestDto, user: User): CommandResponseDto {
        val commandHandler = commandHandlerFactory.create(requestBody.commandType)
        val applicationUser = checkNotNull(userService.findUserByUsername(user.username))
        val result = commandHandler.handle(applicationUser)
        return CommandResponseDto(result)
    }
}
