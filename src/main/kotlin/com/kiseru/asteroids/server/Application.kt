package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.service.RoomService
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class Application(private val port: Int) {

    private lateinit var serverSocket: ServerSocket

    suspend fun startServer() = coroutineScope {
        log.info("Server started")
        serverSocket = withContext(Dispatchers.IO) {
            ServerSocket(port)
        }
        launch {
            startAcceptingConnections()
        }

        val scanner = Scanner(System.`in`)
        while (true) {
            val command = withContext(Dispatchers.IO) {
                scanner.nextLine()
            }
            when (command) {
                "rating" -> {
                    for (room in RoomService.rooms) {
                        println(room.getRating())
                    }
                }

                "gamefield" -> {
                    for (room in RoomService.rooms) {
                        println(room.game.screen.display())
                    }
                }

                "exit" -> break
            }
        }
    }

    private suspend fun startAcceptingConnections() = coroutineScope {
        log.info("Started accepting new connections")
        while (true) {
            val newConnection = withContext(Dispatchers.IO) {
                log.info("Waiting for new connection")
                val socket = serverSocket.accept()
                log.info("Accepted new connection")
                socket
            }
            launch {
                handleNewConnection(newConnection)
            }
        }
    }

    private fun handleNewConnection(newConnection: Socket) {
        log.info("Started handling new connection")
        val notFullRoom = RoomService.getNotFullRoom()
        val user = User(newConnection, notFullRoom)
        user.start()
    }

    companion object {

        private val log = LoggerFactory.getLogger(Application::class.java)
    }
}

fun main() = runBlocking {
    val application = Application(6501)
    application.startServer()
}