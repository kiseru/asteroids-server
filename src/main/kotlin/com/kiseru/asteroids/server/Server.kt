package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Server(private val port: Int) {

    private lateinit var serverSocket: ServerSocket

    suspend fun up() = coroutineScope {
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
                    for (room in rooms) {
                        println(room.getRating())
                    }
                }

                "gamefield" -> {
                    for (room in rooms) {
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
        val notFullRoom = getNotFullRoom()
        val user = User(newConnection, notFullRoom)
        user.start()
    }

    companion object {

        private val log = LoggerFactory.getLogger(Server::class.java)

        private val rooms = mutableListOf<Room>()

        private var notFullRoom = Room()

        private val lock = ReentrantLock()

        fun getNotFullRoom(): Room {
            lock.withLock {
                if (!notFullRoom.isFull) {
                    return notFullRoom
                }

                rooms.add(notFullRoom)
                notFullRoom = Room()
                return notFullRoom
            }
        }
    }
}