package com.kiseru.asteroids.server.factory.impl

import com.kiseru.asteroids.server.factory.MessageSenderServiceFactory
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.impl.MessageSenderServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Socket

class MessageSenderServiceFactoryImpl : MessageSenderServiceFactory {

    override suspend fun create(socket: Socket): MessageSenderService = withContext(Dispatchers.IO) {
        MessageSenderServiceImpl(socket.getOutputStream())
    }
}