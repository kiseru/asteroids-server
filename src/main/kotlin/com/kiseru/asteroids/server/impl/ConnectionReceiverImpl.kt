package com.kiseru.asteroids.server.impl

import com.kiseru.asteroids.server.ConnectionReceiver
import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.handler.impl.RoomHandlerImpl
import com.kiseru.asteroids.server.handler.impl.UserHandlerImpl
import com.kiseru.asteroids.server.logics.Game
import com.kiseru.asteroids.server.logics.Screen
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates
import com.kiseru.asteroids.server.logics.auxiliary.Direction
import com.kiseru.asteroids.server.model.Asteroid
import com.kiseru.asteroids.server.logics.models.Garbage
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.room.RoomStatus
import com.kiseru.asteroids.server.service.RoomService
import java.io.IOException
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.Random
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

private const val GARBAGE_AMOUNT = 150
private const val ASTEROIDS_AMOUNT = 150
private const val GAME_FIELD_HEIGHT = 30
private const val GAME_FIELD_WIDTH = 30

class ConnectionReceiverImpl(
    private val serverSocket: ServerSocket,
    private val roomService: RoomService,
) : ConnectionReceiver {

    override fun acceptConnections(): Unit =
        connections()
            .forEach(::handleConnection)

    private fun connections(): Sequence<Socket> =
        sequence {
            while (true) {
                val socket = serverSocket.accept()
                yield(socket)
            }
        }

    private fun handleConnection(socket: Socket) {
        try {
            val inputStream = socket.getInputStream()
            val outputStream = socket.getOutputStream()
            val printWriter = PrintWriter(outputStream, true)
            val reader = inputStream.bufferedReader()
            handleConnection(reader::readLine, printWriter::println)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun handleConnection(onMessageReceive: () -> String, onMessageSend: (String) -> Unit) {
        try {
            val username = promptUsername(onMessageReceive, onMessageSend)
            val user = User(Random().nextInt(100), username)
            thread { handleUser(user, onMessageReceive, onMessageSend) }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun handleUser(user: User, onMessageReceive: () -> String, onMessageSend: (String) -> Unit) {
        val userHandler = UserHandlerImpl(user, onMessageSend)
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        println("${user.username} has joined the server!")
        val room = createRoom(user, onMessageSend)
        userHandler.onRoomJoin(room.name)
        try {
            userHandler.onSendInstructions()
            val roomHandler = RoomHandlerImpl(room, lock, condition, roomService)
            thread { roomHandler.handle() }
            lock.withLock {
                while (room.status != RoomStatus.GAMING) {
                    condition.await()
                }
            }

            userHandler.onSpaceshipChangeDirection(Direction.UP)
            while (room.status != RoomStatus.FINISHED && user.isAlive) {
                val userMessage = onMessageReceive()
                when (userMessage) {
                    "go" -> {
                        userHandler.onSpaceshipMove()
                        room.game.refresh()
                        userHandler.onSendScore()
                    }

                    "left" -> {
                        userHandler.onSpaceshipChangeDirection(Direction.LEFT)
                        room.game.refresh()
                        userHandler.onSuccess()
                    }

                    "right" -> {
                        userHandler.onSpaceshipChangeDirection(Direction.RIGHT)
                        room.game.refresh()
                        userHandler.onSuccess()
                    }

                    "up" -> {
                        userHandler.onSpaceshipChangeDirection(Direction.UP)
                        room.game.refresh()
                        userHandler.onSuccess()
                    }

                    "down" -> {
                        userHandler.onSpaceshipChangeDirection(Direction.DOWN)
                        room.game.refresh()
                        userHandler.onSuccess()
                    }

                    "isAsteroid" -> userHandler.onIsAsteroid()

                    "isGarbage" -> userHandler.onIsGarbage()

                    "isWall" -> userHandler.onIsWall()

                    "GAME_FIELD" -> roomService.writeGameField(room, onMessageSend)

                    else -> userHandler.onUnknownCommand()
                }
                userHandler.onIncrementSteps()
            }
        } catch (e: IOException) {
            println("Connection problems with user " + user.username)
        } finally {
            user.setIsAlive(false)
            lock.withLock {
                val aliveUsersCount = room.getUsers().count { it.isAlive }
                if (aliveUsersCount == 0) {
                    room.status = RoomStatus.FINISHED
                }
                condition.signalAll()
            }
        }
    }

    private fun promptUsername(onReceiveMessage: () -> String, onMessageSend: (String) -> Unit): String {
        onMessageSend("Welcome To Asteroids Server")
        onMessageSend("Please, introduce yourself!")
        return onReceiveMessage()
    }

    private fun createRoom(user: User, onMessageSend: (String) -> Unit): Room {
        val roomId = UUID.randomUUID()
        val game = Game(Screen(GAME_FIELD_WIDTH, GAME_FIELD_HEIGHT), GARBAGE_AMOUNT)
        generateGarbage(game)
        generateAsteroids(game)
        val room = Room(roomId, roomId.toString(), game, 1)
        room.addUser(user, onMessageSend)
        return room
    }

    private fun generateGarbage(game: Game) {
        freeCoordinates(game)
            .take(GARBAGE_AMOUNT)
            .forEach {
                val garbage = Garbage(it)
                game.addPoint(garbage)
            }
    }

    private fun generateAsteroids(game: Game) {
        freeCoordinates(game)
            .take(ASTEROIDS_AMOUNT)
            .forEach {
                val asteroid = Asteroid(it)
                game.addPoint(asteroid)
            }
    }

    private fun freeCoordinates(game: Game): Sequence<Coordinates> =
        sequence {
            val coordinates = game.generateUniqueRandomCoordinates()
            yield(coordinates)
        }
}
