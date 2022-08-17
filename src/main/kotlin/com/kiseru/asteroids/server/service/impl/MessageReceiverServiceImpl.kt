package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.service.MessageReceiverService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class MessageReceiverServiceImpl(inputStream: InputStream) : MessageReceiverService {

    private val reader = BufferedReader(InputStreamReader(inputStream))

    override suspend fun receive(): String? = withContext(Dispatchers.IO) {
        reader.readLine()
    }
}