package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.factory.MessageReceiverServiceFactory
import com.kiseru.asteroids.server.factory.MessageSenderServiceFactory
import com.kiseru.asteroids.server.handler.CommandHandlerFactory
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.Socket

@Service
class UserServiceImpl(
    private val commandHandlerFactory: CommandHandlerFactory,
    private val messageReceiverServiceFactory: MessageReceiverServiceFactory,
    private val messageSenderServiceFactory: MessageSenderServiceFactory,
) : UserService {

    override suspend fun authorizeUser(socket: Socket, room: Room): User {
        val messageReceiverService = messageReceiverServiceFactory.create(socket)
        val messageSenderService = messageSenderServiceFactory.create(socket)
        try {
            messageSenderService.sendWelcomeMessage()
            val username = messageReceiverService.receive()
            log.info("{} has joined the server", username)
            val user = User(username, room, socket, messageReceiverService, messageSenderService, commandHandlerFactory)
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

