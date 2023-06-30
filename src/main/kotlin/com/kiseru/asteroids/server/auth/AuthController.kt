package com.kiseru.asteroids.server.auth

import com.kiseru.asteroids.server.auth.dto.SingUpRequest
import com.kiseru.asteroids.server.auth.dto.SingUpResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/auth")
@RestController
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping
    suspend fun singUp(@RequestBody singUpRequest: SingUpRequest): SingUpResponse =
        authService.singUp(singUpRequest)
}
