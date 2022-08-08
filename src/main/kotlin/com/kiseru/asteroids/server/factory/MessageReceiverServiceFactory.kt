package com.kiseru.asteroids.server.factory

import com.kiseru.asteroids.server.service.MessageReceiverService
import java.net.Socket

interface MessageReceiverServiceFactory {

    suspend fun create(socket: Socket): MessageReceiverService
}