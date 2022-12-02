package com.kiseru.asteroids.server.factory.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.kiseru.asteroids.server.awaitOutputStream
import com.kiseru.asteroids.server.factory.MessageSenderServiceFactory
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.impl.MessageSenderServiceImpl
import org.springframework.stereotype.Component
import java.net.Socket

@Component
class MessageSenderServiceFactoryImpl(
    private val objectMapper: ObjectMapper,
) : MessageSenderServiceFactory {

    override suspend fun create(socket: Socket): MessageSenderService =
        MessageSenderServiceImpl(objectMapper, socket.awaitOutputStream())
}