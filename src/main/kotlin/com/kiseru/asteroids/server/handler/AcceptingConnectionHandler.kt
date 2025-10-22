package com.kiseru.asteroids.server.handler

import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.BlockingQueue

class AcceptingConnectionHandler(private val connectionQueue: BlockingQueue<Socket>) {

    fun handle(port: Int) {
        val serverSocket = ServerSocket(port)
        generateSequence { serverSocket.accept() }
            .forEach { connectionQueue.put(it) }
    }
}