package com.kiseru.asteroids.server.command

import com.kiseru.asteroids.server.command.dto.CommandRequestDto
import com.kiseru.asteroids.server.command.dto.CommandResponseDto
import org.springframework.security.core.userdetails.User

interface CommandService {

    suspend fun handleCommand(requestBody: CommandRequestDto, user: User): CommandResponseDto
}
