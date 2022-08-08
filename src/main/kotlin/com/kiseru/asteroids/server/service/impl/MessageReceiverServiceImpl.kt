package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.service.MessageReceiverService
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class MessageReceiverServiceImpl(inputStream: InputStream) : MessageReceiverService {

    private val reader = BufferedReader(InputStreamReader(inputStream))

    override fun receive(): String {
        return reader.readLine()
    }
}