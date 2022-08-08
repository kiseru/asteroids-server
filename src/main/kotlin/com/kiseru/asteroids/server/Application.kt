package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.service.RoomService
import com.kiseru.asteroids.server.service.impl.MessageReceiverServiceImpl
import com.kiseru.asteroids.server.service.impl.MessageSenderServiceImpl
import com.kiseru.asteroids.server.service.impl.RoomServiceImpl
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.Executors

const val PORT = 6501

class Application(private val roomService: RoomService) {

    private lateinit var serverSocket: ServerSocket

    suspend fun startServer() = coroutineScope {
        log.info("Server started")
        serverSocket = withContext(Dispatchers.IO) {
            ServerSocket(PORT)
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
        val user = authorizeUser(newConnection)
        executorService.execute(user)
    }

    private suspend fun authorizeUser(socket: Socket): User {
        val messageReceiverService = withContext(Dispatchers.IO) { MessageReceiverServiceImpl(socket.getInputStream()) }
        val messageSenderService = withContext(Dispatchers.IO) { MessageSenderServiceImpl(socket.getOutputStream()) }
        val room = roomService.getNotFullRoom()
        try {
            messageSenderService.sendWelcomeMessage()
            val username = withContext(Dispatchers.IO) { messageReceiverService.receive() }
            log.info("{} has joined the server", username)
            val user = User(username, room, socket, messageReceiverService, messageSenderService)
            messageSenderService.sendInstructions(user)
            return user
        } catch (e: IOException) {
            log.error("Failed to authorize user", e)
            throw e
        }
    }

    companion object {

        private val log = LoggerFactory.getLogger(Application::class.java)

        private val executorService = Executors.newCachedThreadPool()
    }
}

fun main() = runBlocking {
    val application = Application(RoomServiceImpl)
    application.startServer()
}