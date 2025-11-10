package com.kiseru.asteroids.server.handler

import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.BlockingQueue

class AcceptingConnectionHandler(private val connectionQueue: BlockingQueue<Socket>) {

    fun handle(port: Int) {
        require(port in 1..65535) { "Port must be between 1 and 65535, but was $port" }

        val serverSocket = ServerSocket(port)

        try {
            generateSequence { serverSocket.accept() }
                .forEach { connectionQueue.put(it) }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw e
        } finally {
            serverSocket.close()
        }
    }
}