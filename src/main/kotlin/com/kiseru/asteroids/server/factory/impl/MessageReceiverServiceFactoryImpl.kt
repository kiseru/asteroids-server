package com.kiseru.asteroids.server.factory.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kiseru.asteroids.server.awaitInputStream
import com.kiseru.asteroids.server.factory.MessageReceiverServiceFactory
import com.kiseru.asteroids.server.service.MessageReceiverService
import com.kiseru.asteroids.server.service.impl.MessageReceiverServiceImpl
import org.springframework.stereotype.Component
import java.net.Socket

@Component
class MessageReceiverServiceFactoryImpl : MessageReceiverServiceFactory {

    override suspend fun create(socket: Socket): MessageReceiverService =
        MessageReceiverServiceImpl(jacksonObjectMapper(), socket.awaitInputStream())
}
