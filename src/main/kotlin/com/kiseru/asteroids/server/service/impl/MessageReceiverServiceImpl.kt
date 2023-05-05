package com.kiseru.asteroids.server.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.kiseru.asteroids.server.awaitReadLine
import com.kiseru.asteroids.server.model.Message
import com.kiseru.asteroids.server.service.MessageReceiverService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class MessageReceiverServiceImpl(
    var objectMapper: ObjectMapper,
    inputStream: InputStream,
) : MessageReceiverService {

    private val reader = BufferedReader(InputStreamReader(inputStream))

    override suspend fun receive(): String = reader.awaitReadLine()

    override suspend fun receiveMessage(): Message {
        val line = reader.awaitReadLine()
        return objectMapper.readValue(line, Message::class.java)
    }
    override fun receivingMessagesFlow(): Flow<Message> {
        return flow {
            while (true) {
                emit(receiveMessage())
            }
        }
    }
}
