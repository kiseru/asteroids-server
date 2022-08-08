package com.kiseru.asteroids.server.factory.impl

import com.kiseru.asteroids.server.factory.MessageReceiverServiceFactory
import com.kiseru.asteroids.server.service.MessageReceiverService
import com.kiseru.asteroids.server.service.impl.MessageReceiverServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Socket

class MessageReceiverServiceFactoryImpl : MessageReceiverServiceFactory {

    override suspend fun create(socket: Socket): MessageReceiverService = withContext(Dispatchers.IO) {
        MessageReceiverServiceImpl(socket.getInputStream())
    }
}