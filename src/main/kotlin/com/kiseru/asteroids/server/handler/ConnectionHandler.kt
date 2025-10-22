package com.kiseru.asteroids.server.handler

import java.net.Socket

class ConnectionHandler(socket: Socket) {

    private val reader = socket.getInputStream().bufferedReader()
    private val writer = socket.getOutputStream().bufferedWriter()

    fun receiveCommand(): Command? =
        try {
            val command = receiveMessage()
            Command.valueOf(command)
        } catch (_: IllegalArgumentException) {
            null
        }

    fun receiveMessage(): String =
        reader.readLine()

    fun sendMessage(message: String) {
        writer.appendLine(message)
        writer.flush()
    }
}

