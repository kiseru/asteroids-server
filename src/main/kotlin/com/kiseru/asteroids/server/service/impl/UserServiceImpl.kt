package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.dto.TokenDto
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.TokenService
import com.kiseru.asteroids.server.service.UserService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.Socket
import java.util.*

@Service
class UserServiceImpl(
    private val tokenService: TokenService,
) : UserService {

    private val userStorage = mutableMapOf<String, User>()

    override suspend fun authorizeUser(
        socket: Socket,
        room: Room,
        messageSenderService: MessageSenderService,
        username: String,
    ): User {
        log.info("{} has joined the server", username)
        val userId = generateUniqueUserId()
        val user = User(userId, username, room, socket)
        val tokenDto = TokenDto(tokenService.generateToken(user))
        messageSenderService.send(Json.encodeToString(tokenDto))
        messageSenderService.sendInstructions(user)
        return user
    }

    private fun generateUniqueUserId(): String = generateSequence { UUID.randomUUID().toString() }
        .dropWhile { userStorage.containsKey(it) }
        .first()

    companion object {

        private val log = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }
}

