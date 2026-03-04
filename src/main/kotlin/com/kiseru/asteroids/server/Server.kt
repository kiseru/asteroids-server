package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.handler.CreatingGameHandler
import rx.Observable
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class Server(private val port: Int) {

    fun up() {
        val connectionQueue = LinkedBlockingQueue<Socket>()
        connections(port)
            .subscribe(
                { connectionQueue.put(it) },
                { println("Error in accepting connections: ${it.message}") }
            )

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

    private fun connections(port: Int): Observable<Socket> {
        require(port in 1..65535) { "Port must be between 1 and 65535, but was $port" }

        return Observable.create {
            thread(name = "AcceptingConnectionThread") {
                ServerSocket(port)
                    .use { serverSocket ->
                        try {
                            while (!(it.isUnsubscribed)) {
                                val socket = serverSocket.accept()
                                it.onNext(socket)
                            }
                            serverSocket.close()
                            it.onCompleted()
                        } catch (e: InterruptedException) {
                            Thread.currentThread().interrupt()
                            it.onError(e)
                        }
                    }
            }
        }
    }
}
