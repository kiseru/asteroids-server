package com.kiseru.asteroids.server.command

import com.kiseru.asteroids.server.command.dto.CommandRequestDto
import com.kiseru.asteroids.server.command.dto.CommandResponseDto
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/commands")
class CommandController(private val commandService: CommandService) {

    @PostMapping
    suspend fun handleCommand(
        @RequestBody requestBody: CommandRequestDto,
        @AuthenticationPrincipal user: User,
    ): CommandResponseDto =
        commandService.handleCommand(requestBody, user)
}
