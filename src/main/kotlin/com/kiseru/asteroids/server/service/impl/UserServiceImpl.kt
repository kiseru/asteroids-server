package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.Socket

object UserServiceImpl : UserService {

    private val log = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override suspend fun authorizeUser(socket: Socket, room: Room): User {
        val messageReceiverService = withContext(Dispatchers.IO) { MessageReceiverServiceImpl(socket.getInputStream()) }
        val messageSenderService = withContext(Dispatchers.IO) { MessageSenderServiceImpl(socket.getOutputStream()) }
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
}