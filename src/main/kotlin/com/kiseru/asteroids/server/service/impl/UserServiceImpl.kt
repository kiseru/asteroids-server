package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.service.RoomService
import com.kiseru.asteroids.server.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.Socket

class UserServiceImpl(
    private val roomService: RoomService
) : UserService {

    override suspend fun authorizeUser(socket: Socket): User {
        val messageReceiverService = withContext(Dispatchers.IO) { MessageReceiverServiceImpl(socket.getInputStream()) }
        val messageSenderService = withContext(Dispatchers.IO) { MessageSenderServiceImpl(socket.getOutputStream()) }
        val room = roomService.getNotFullRoom()
        try {
            messageSenderService.sendWelcomeMessage()
            val username = withContext(Dispatchers.IO) { messageReceiverService.receive() }
            log.info("{} has joined the server", username)
            val user = User(username, room, socket, messageReceiverService, messageSenderService)
            messageSenderService.sendInstructions(user)
            return user
        } catch (e: IOException) {
            log.error("Failed to authorize user", e)
            throw e
        }
    }

    companion object {

        private val log = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }
}