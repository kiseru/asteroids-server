package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.exception.GameFinishedException
import com.kiseru.asteroids.server.handler.CommandHandlerFactory
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.RoomService
import com.kiseru.asteroids.server.service.UserService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.ServerSocket
import java.net.Socket
import java.util.*

@Component
class Server(
    private val commandHandlerFactory: CommandHandlerFactory,
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

    private suspend fun startAcceptingConnections() = newConnections()
        .onStart { log.info("Started accepting new connections") }
        .collect {
            coroutineScope {
                launch { handleNewConnection(it) }
            }
        }

    private suspend fun newConnections(): Flow<Socket> = flow {
        while (true) {
            emit(serverSocket.awaitAccept())
        }
    }

    private suspend fun handleNewConnection(newConnection: Socket) = coroutineScope {
        log.info("Started handling new connection")
        val room = roomService.getNotFullRoom()
        val user = userService.authorizeUser(newConnection, room)
        addUser(room, user)
        roomService.sendMessageToUsers(room, "User ${user.username} has joined the room.")
        launch {
            startRoom(room)
        }
        launch {
            runUser(user)
        }
    }

    fun addUser(room: Room, user: User) {
        check(room.users.size < Room.MAX_USERS)
        room.status = Room.Status.WAITING_CONNECTIONS
        room.game.registerSpaceshipForUser(user)
        room.users = room.users + user
    }

    private suspend fun runUser(user: User) {
        awaitCreatingSpaceship(user)
        try {
            while (!user.room.isGameFinished && user.isAlive) {
                val command = user.messageReceiverService.receive()
                handleCommand(user, command)
                incrementSteps(user)
                checkIsAlive(user)
            }
        } finally {
            user.isAlive = false
            user.room.setGameFinished()
        }
    }

    private suspend fun awaitCreatingSpaceship(user: User) {
        while (user.spaceship == null) {
            yield()
        }
    }

    private suspend fun handleCommand(user: User, command: String) {
        val commandHandler = commandHandlerFactory.create(command)
        commandHandler.handle(user)
    }

    private fun incrementSteps(user: User) {
        if (!user.isAlive) {
            throw GameFinishedException()
        }

        user.steps++
    }

    private suspend fun checkIsAlive(user: User) {
        if (user.steps >= 1500 || user.score < 0) {
            died(user)
        }
    }

    private suspend fun died(user: User) {
        user.isAlive = false
        user.messageSenderService.sendGameOver(user.score)
    }

    private suspend fun startRoom(room: Room) {
        awaitUsers(room)
        roomService.sendMessageToUsers(room, "start")
        room.status = Room.Status.GAMING
        refreshRoom(room)
        roomService.awaitEndgame(room)
        val rating = room.rating
        roomService.sendMessageToUsers(room, "finish\n$rating")
        log.info("Room $room released! Rating table:\n$rating")
    }

    private suspend fun awaitUsers(room: Room) {
        while (room.users.count() < Room.MAX_USERS) {
            yield()
        }
    }

    private fun refreshRoom(room: Room) {
        room.game.refresh()
    }

    companion object {

        private val log = LoggerFactory.getLogger(Server::class.java)
    }
}