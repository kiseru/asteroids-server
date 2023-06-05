package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.UserService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl : UserService {

    private val userStorageMutex = Mutex()

    private val userStorage = mutableMapOf<UUID, User>()

    override suspend fun createUser(username: String, room: Room): User {
        userStorageMutex.withLock {
            val userId = generateUniqueUserId()
            val user = User(userId, username, room.id, )
            userStorage[userId] = user
            return user
        }
    }

    override fun findUserById(userId: UUID): User? {
        return userStorage[userId]
    }

    private fun generateUniqueUserId(): UUID = generateSequence { UUID.randomUUID() }
        .dropWhile { userStorage.containsKey(it) }
        .first()
}

