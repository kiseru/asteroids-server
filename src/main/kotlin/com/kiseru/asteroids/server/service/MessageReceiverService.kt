package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageReceiverService {

    suspend fun receive(): String

    suspend fun receiveMessage(): Message

    fun receivingMessagesFlow(): Flow<Message>
}

