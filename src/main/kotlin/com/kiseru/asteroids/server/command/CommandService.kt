package com.kiseru.asteroids.server.command

import com.kiseru.asteroids.server.command.dto.CommandResponseDto
import org.springframework.security.core.userdetails.User

interface CommandService {

    suspend fun handleDownCommand(user: User): CommandResponseDto

    suspend fun handleExitCommand(user: User): CommandResponseDto

    suspend fun handleGoCommand(user: User): CommandResponseDto

    suspend fun handleIsAsteroidCommand(user: User): CommandResponseDto

    suspend fun handleIsGarbageCommand(user: User): CommandResponseDto

    suspend fun handleIsWallCommand(user: User): CommandResponseDto

    suspend fun handleLeftCommand(user: User): CommandResponseDto

    suspend fun handleRightCommand(user: User): CommandResponseDto

    suspend fun handleUpCommand(user: User): CommandResponseDto
}
