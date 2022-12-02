package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.model.Room
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

@Component
class Server(
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

    private suspend fun handleNewConnection(newConnection: Socket) = coroutineScope {
        log.info("Started handling new connection")
        val room = roomService.getNotFullRoom()
        val user = userService.authorizeUser(newConnection, room)
        room.addUser(user)
        roomService.sendMessageToUsers(room, "User ${user.username} has joined the room.")
        launch {
            startRoom(room)
        }
        launch {
            user.run()
        }
    }

    private suspend fun startRoom(room: Room) {
        room.awaitUsers()
        roomService.sendMessageToUsers(room, "start")
        room.status = Room.Status.GAMING
        room.refresh()
        room.awaitEndgame()
        val rating = room.rating
        roomService.sendMessageToUsers(room, "finish\n$rating")
        log.info("Room $room released! Rating table:\n$rating")
    }

    companion object {

        private val log = LoggerFactory.getLogger(Server::class.java)
    }
}