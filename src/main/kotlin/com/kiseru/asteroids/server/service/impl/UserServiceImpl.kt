package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.UserService
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl : UserService {

    private val userStorage = mutableMapOf<String, User>()

    override suspend fun createUser(username: String): User = User(generateUniqueUserId(), username)

    private fun generateUniqueUserId(): String = generateSequence { UUID.randomUUID().toString() }
        .dropWhile { userStorage.containsKey(it) }
        .first()
}

