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
        thread(name = "AcceptingThread") {
            try {
                acceptingConnectionHandler.handle(port)
            } catch (e: Exception) {
                println("Error in accepting connections: ${e.message}")
            }
        }

        val creatingGameHandler = CreatingGameHandler(connectionQueue)
        thread(name = "CreatingGameThread") {
            try {
                creatingGameHandler.handle()
            } catch (e: Exception) {
                println("Error in creating games: ${e.message}")
            }
        }

        println("Server started on port $port")
    }
}
