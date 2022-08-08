package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.service.RoomService
import com.kiseru.asteroids.server.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.Executors

@Component
class Server(
    private val roomService: RoomService,
    private val userService: UserService,
    @Value("\${asteroids.server.port}") private val port: Int,
) {

    private lateinit var serverSocket: ServerSocket

    suspend fun startServer() = coroutineScope {
        log.info("Server started at port $port")
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
                    for (room in roomService.rooms) {
                        println(roomService.getRoomRating(room))
                    }
                }

                "gamefield" -> {
                    for (room in roomService.rooms) {
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

    private suspend fun handleNewConnection(newConnection: Socket) {
        log.info("Started handling new connection")
        val user = userService.authorizeUser(newConnection)
        executorService.execute(user)
    }

    companion object {

        private val log = LoggerFactory.getLogger(Server::class.java)

        private val executorService = Executors.newCachedThreadPool()
    }
}