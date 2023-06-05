package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.command.factory.CommandHandlerFactory
import com.kiseru.asteroids.server.dto.TokenDto
import com.kiseru.asteroids.server.exception.GameFinishedException
import com.kiseru.asteroids.server.factory.MessageReceiverServiceFactory
import com.kiseru.asteroids.server.factory.MessageSenderServiceFactory
import com.kiseru.asteroids.server.game.GameService
import com.kiseru.asteroids.server.model.Message
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.MessageReceiverService
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.RoomService
import com.kiseru.asteroids.server.service.TokenService
import com.kiseru.asteroids.server.service.UserService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
    private val gameService: GameService,
    private val commandHandlerFactory: CommandHandlerFactory,
    private val roomService: RoomService,
    private val serverSocket: ServerSocket,
    private val userService: UserService,
    private val tokenService: TokenService,
) {

    suspend fun startServer() = coroutineScope {
        launch {
            startAcceptingConnections()
        }

        val scanner = Scanner(System.`in`)
        while (true) {
            when (scanner.awaitNextLine()) {
                "rating" -> roomService.showAllRatings()
                "gamefield" -> roomService.showAllGameFields()
                "exit" -> {
                    serverSocket.awaitClose()
                    break
                }
            }
        }
    }

    private suspend fun startAcceptingConnections() = coroutineScope {
        newConnections()
            .onStart { log.info("Started accepting new connections") }
            .catch { log.info("Finished accepting connections") }
            .collect {
                launch { handleNewConnection(it) }
            }
    }

    private suspend fun newConnections(): Flow<Socket> = channelFlow {
        while (true) {
            send(serverSocket.awaitAccept())
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
            val user = userService.createUser(username, room)
            val tokenDto = TokenDto(tokenService.generateToken(user))
            messageSenderService.send(Json.encodeToString(tokenDto))
            messageSenderService.sendInstructions(user)
            user
        } catch (e: IOException) {
            log.error("Failed to authorize user", e)
            throw e
        }
        addUser(room, user, messageSenderService)
        if (room.users.size == Room.MAX_USERS) {
            launch {
                startRoom(room)
            }
        }
        roomService.sendMessageToUsers(room, "User ${user.username} has joined the room.")
        launch {
            runUser(user, room, messageSenderService, messageReceiverService) { newConnection.awaitClose() }
        }
    }

    private suspend fun addUser(room: Room, user: User, messageSenderService: MessageSenderService) {
        check(room.users.size < Room.MAX_USERS)
        room.status = Room.Status.WAITING_CONNECTIONS
        room.users = room.users + user
        room.messageSenderServices += messageSenderService
        gameService.registerSpaceshipForUser(room.game, user, room)
    }

    private suspend fun runUser(
        user: User,
        room: Room,
        messageSenderService: MessageSenderService,
        messageReceiverService: MessageReceiverService,
        closeSocket: suspend () -> Unit,
    ) {
        messageReceiverService.receivingMessagesFlow()
            .onCompletion {
                user.isAlive = false
                room.setGameFinished()
            }
            .takeWhile { !room.isGameFinished && user.isAlive }
            .collect { message ->
                handleMessage(message, messageSenderService, closeSocket)
                incrementSteps(user)
                checkIsAlive(user, messageSenderService)
            }
    }

    private suspend fun handleMessage(
        message: Message,
        messageSenderService: MessageSenderService,
        closeSocket: suspend () -> Unit,
    ) {
        val userId = tokenService.getUserId(message.token)
        handleCommand(userId, messageSenderService, message.command, closeSocket)
    }

    private suspend fun handleCommand(
        userId: UUID,
        messageSenderService: MessageSenderService,
        command: String,
        closeSocket: suspend () -> Unit,
    ) {
        val commandHandler = commandHandlerFactory.create(command)
        commandHandler.handle(userId, messageSenderService, closeSocket)
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
        roomService.sendMessageToUsers(room, "start")
        room.status = Room.Status.GAMING
        room.game.refresh()
        roomService.awaitEndgame(room)
        val rating = room.rating
        roomService.sendMessageToUsers(room, "finish\n$rating")
        log.info("Room $room released! Rating table:\n$rating")
    }

    companion object {

        private val log = LoggerFactory.getLogger(Server::class.java)
    }
}
