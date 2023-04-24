package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.awaitReadLine
import com.kiseru.asteroids.server.service.MessageReceiverService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class MessageReceiverServiceImpl(inputStream: InputStream) : MessageReceiverService {

    private val reader = BufferedReader(InputStreamReader(inputStream))

    override suspend fun receive(): String = reader.awaitReadLine()

    override fun receivingFlow(): Flow<String> {
        return flow {
            while (true) {
                emit(receive())
            }
        }
    }
}
