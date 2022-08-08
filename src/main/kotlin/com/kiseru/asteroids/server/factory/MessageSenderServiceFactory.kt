package com.kiseru.asteroids.server.factory

import com.kiseru.asteroids.server.service.MessageSenderService
import java.net.Socket

interface MessageSenderServiceFactory {

    suspend fun create(socket: Socket): MessageSenderService
}