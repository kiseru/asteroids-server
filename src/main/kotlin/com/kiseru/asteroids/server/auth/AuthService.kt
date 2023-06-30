package com.kiseru.asteroids.server.auth

import com.kiseru.asteroids.server.auth.dto.SingUpRequest
import com.kiseru.asteroids.server.auth.dto.SingUpResponse

interface AuthService {

    suspend fun singUp(singUpRequest: SingUpRequest): SingUpResponse
}
