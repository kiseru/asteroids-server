package com.kiseru.asteroids.server.command

import com.kiseru.asteroids.server.command.dto.CommandResponseDto
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/commands")
class CommandController(private val commandService: CommandService) {

    @PostMapping("down")
    suspend fun handleDownCommand(@AuthenticationPrincipal user: User): CommandResponseDto =
        commandService.handleDownCommand(user)

    @PostMapping("exit")
    suspend fun handleExitCommand(@AuthenticationPrincipal user: User): CommandResponseDto =
        commandService.handleExitCommand(user)

    @PostMapping("go")
    suspend fun handleGoCommand(@AuthenticationPrincipal user: User): CommandResponseDto =
        commandService.handleGoCommand(user)

    @PostMapping("is-asteroid")
    suspend fun handleIsAsteroidCommand(@AuthenticationPrincipal user: User): CommandResponseDto =
        commandService.handleIsAsteroidCommand(user)

    @PostMapping("is-garbage")
    suspend fun handleIsGarbageCommand(@AuthenticationPrincipal user: User): CommandResponseDto =
        commandService.handleIsGarbageCommand(user)

    @PostMapping("is-wall")
    suspend fun handleIsWallCommand(@AuthenticationPrincipal user: User): CommandResponseDto =
        commandService.handleIsWallCommand(user)

    @PostMapping("left")
    suspend fun handleLeftCommand(@AuthenticationPrincipal user: User): CommandResponseDto =
        commandService.handleLeftCommand(user)

    @PostMapping("right")
    suspend fun handleRightCommand(@AuthenticationPrincipal user: User): CommandResponseDto =
        commandService.handleRightCommand(user)

    @PostMapping("up")
    suspend fun handleUpCommand(@AuthenticationPrincipal user: User): CommandResponseDto =
        commandService.handleUpCommand(user)
}
