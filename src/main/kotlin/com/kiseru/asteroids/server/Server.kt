package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.handler.AcceptingConnectionHandler
import com.kiseru.asteroids.server.handler.CreatingGameHandler
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class Server(private val port: Int) {

    fun up() {
        val connectionQueue = LinkedBlockingQueue<Socket>()
        val acceptingConnectionHandler = AcceptingConnectionHandler(connectionQueue)
        thread { acceptingConnectionHandler.handle(port) }
        val creatingGameHandler = CreatingGameHandler(connectionQueue)
        thread { creatingGameHandler.handle() }
    }
}
