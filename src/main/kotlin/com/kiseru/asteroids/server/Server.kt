package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.service.RoomService
import com.kiseru.asteroids.server.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.ExecutorService

@Component
class Server(
    private val mainExecutorService: ExecutorService,
    private val roomService: RoomService,
    private val serverSocket: ServerSocket,
    private val userService: UserService,
) {

    suspend fun startServer() = coroutineScope {
        launch {
            startAcceptingConnections()
        }

        val scanner = Scanner(System.`in`)
        while (true) {
            val command = withContext(Dispatchers.IO) {
                scanner.nextLine()
            }
            when (command) {
                "rating" -> roomService.showAllRatings()
                "gamefield" -> roomService.showAllGameFields()
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
        mainExecutorService.execute(user)
    }

    companion object {

        private val log = LoggerFactory.getLogger(Server::class.java)
    }
}