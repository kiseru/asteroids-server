package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.exception.GameFinishedException
import com.kiseru.asteroids.server.factory.MessageReceiverServiceFactory
import com.kiseru.asteroids.server.factory.MessageSenderServiceFactory
import com.kiseru.asteroids.server.handler.CommandHandlerFactory
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.MessageReceiverService
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.RoomService
import com.kiseru.asteroids.server.service.UserService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.*

@Component
class Server(
    private val messageReceiverServiceFactory: MessageReceiverServiceFactory,
    private val messageSenderServiceFactory: MessageSenderServiceFactory,
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
        val messageReceiverService = messageReceiverServiceFactory.create(newConnection)
        val messageSenderService = messageSenderServiceFactory.create(newConnection)
        val user = try {
            messageSenderService.sendWelcomeMessage()
            val username = messageReceiverService.receive()
            userService.authorizeUser(newConnection, room, messageSenderService, username)
        } catch (e: IOException) {
            log.error("Failed to authorize user", e)
            throw e
        }
        addUser(room, user, messageSenderService)
        roomService.sendMessageToUsers(room, "User ${user.username} has joined the room.")
        launch {
            startRoom(room)
        }
        launch {
            runUser(user, messageSenderService, messageReceiverService)
        }
    }

    fun addUser(room: Room, user: User, messageSenderService: MessageSenderService) {
        check(room.users.size < Room.MAX_USERS)
        room.status = Room.Status.WAITING_CONNECTIONS
        room.game.registerSpaceshipForUser(user)
        room.users = room.users + user
        room.messageSenderServices += messageSenderService
    }

    private suspend fun runUser(
        user: User,
        messageSenderService: MessageSenderService,
        messageReceiverService: MessageReceiverService
    ) {
        awaitCreatingSpaceship(user)
        try {
            while (!user.room.isGameFinished && user.isAlive) {
                val command = messageReceiverService.receive()
                handleCommand(user, messageSenderService, command)
                incrementSteps(user)
                checkIsAlive(user, messageSenderService)
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

    private suspend fun handleCommand(user: User, messageSenderService: MessageSenderService, command: String) {
        val commandHandler = commandHandlerFactory.create(command)
        commandHandler.handle(user, messageSenderService)
    }

    private fun incrementSteps(user: User) {
        if (!user.isAlive) {
            throw GameFinishedException()
        }

        user.steps++
    }

    private suspend fun checkIsAlive(user: User, messageSenderService: MessageSenderService) {
        if (user.steps >= 1500 || user.score < 0) {
            died(user, messageSenderService)
        }
    }

    private suspend fun died(user: User, messageSenderService: MessageSenderService) {
        user.isAlive = false
        messageSenderService.sendGameOver(user.score)
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