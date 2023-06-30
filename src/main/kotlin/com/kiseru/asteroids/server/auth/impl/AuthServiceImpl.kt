package com.kiseru.asteroids.server.auth.impl

import com.kiseru.asteroids.server.auth.AuthService
import com.kiseru.asteroids.server.auth.dto.SingUpRequest
import com.kiseru.asteroids.server.auth.dto.SingUpResponse
import com.kiseru.asteroids.server.service.RoomService
import com.kiseru.asteroids.server.service.UserService
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val roomService: RoomService,
    private val userService: UserService,
) : AuthService {

    override suspend fun singUp(singUpRequest: SingUpRequest): SingUpResponse {
        val room = roomService.getNotFullRoom()
        val user = userService.createUser(singUpRequest.username, room)
        return SingUpResponse(user.username)
    }
}
