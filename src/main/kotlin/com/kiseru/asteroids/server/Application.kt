package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.service.RoomService
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.Executors

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
                        println(RoomService.getRoomRating(room))
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

    private suspend fun handleNewConnection(newConnection: Socket) {
        log.info("Started handling new connection")
        val room = RoomService.getNotFullRoom()
        val inputStream = withContext(Dispatchers.IO) { newConnection.getInputStream() }
        val outputStream = withContext(Dispatchers.IO) { newConnection.getOutputStream() }
        val reader = BufferedReader(InputStreamReader(inputStream))
        val writer = PrintWriter(outputStream)
        val user = authorizeUser(reader, writer, room)
        executorService.execute(user)
    }

    private suspend fun authorizeUser(reader: BufferedReader, writer: PrintWriter, room: Room): User {
        try {
            sendWelcomeMessage(writer)
            val username = withContext(Dispatchers.IO) { reader.readLine() }
            log.info("{} has joined the server", username)
            val user = User(username, room, reader, writer)
            sendInstructions(writer, user)
            return user
        } catch (e: IOException) {
            log.error("Failed to authorize user", e)
            throw e
        }
    }

    private fun sendWelcomeMessage(writer: PrintWriter) {
        writer.println("Welcome To Asteroids Server")
        writer.println("Please, introduce yourself!")
        writer.flush()
    }

    private fun sendInstructions(writer: PrintWriter, user: User) {
        writer.println("You need to keep a space garbage.")
        writer.println("Your ID is ${user.id}")
        writer.println("Good luck, Commander!")
        writer.flush()
    }

    companion object {

        private val log = LoggerFactory.getLogger(Application::class.java)

        private val executorService = Executors.newCachedThreadPool()
    }
}

fun main() = runBlocking {
    val application = Application(6501)
    application.startServer()
}