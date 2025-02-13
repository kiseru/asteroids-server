package com.kiseru.asteroids.server.impl

import com.kiseru.asteroids.server.ConnectionReceiver
import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.handler.impl.RoomHandlerImpl
import com.kiseru.asteroids.server.logics.Game
import com.kiseru.asteroids.server.logics.Screen
import com.kiseru.asteroids.server.logics.auxiliary.Direction
import com.kiseru.asteroids.server.room.Room
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
            thread { handleUser(reader::readLine, printWriter::println) }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun handleUser(onMessageReceive: () -> String, onMessageSend: (String) -> Unit) {
        val username = promptUsername(onMessageReceive, onMessageSend)
        val user = User(Random().nextInt(100), username)
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        println("$username has joined the server!")
        val room = createRoom(user, onMessageSend)
        onMessageSend("You joined the room \"${room.name}\"")
        try {
            sendInstructions(user, onMessageSend)
            val roomHandler = RoomHandlerImpl(room, lock, condition, roomService)
            thread { roomHandler.handle() }

            lock.withLock {
                room.status = RoomStatus.GAMING
                condition.signalAll()
            }

            user.spaceship.direction = Direction.UP
            while (room.status != RoomStatus.FINISHED && user.isAlive) {
                val userMessage = onMessageReceive()
                when (userMessage) {
                    "go" -> {
                        user.spaceship.go()
                        room.game.refresh()
                        onMessageSend(user.score.toString())
                    }

                    "left" -> {
                        user.spaceship.direction = Direction.LEFT
                        room.game.refresh()
                        onMessageSend("success")
                    }

                    "right" -> {
                        user.spaceship.direction = Direction.RIGHT
                        room.game.refresh()
                        onMessageSend("success")
                    }

                    "up" -> {
                        user.spaceship.direction = Direction.UP
                        room.game.refresh()
                        onMessageSend("success")
                    }

                    "down" -> {
                        user.spaceship.direction = Direction.DOWN
                        room.game.refresh()
                        onMessageSend("success")
                    }

                    "isAsteroid" -> {
                        onMessageSend(if (user.spaceship.courseChecker.isAsteroid) "t" else "f")
                    }

                    "isGarbage" -> {
                        onMessageSend(if (user.spaceship.courseChecker.isGarbage) "t" else "f")
                    }

                    "isWall" -> {
                        onMessageSend(if (user.spaceship.courseChecker.isWall) "t" else "f")
                    }

                    "GAME_FIELD" -> {
                        roomService.writeGameField(room, onMessageSend)
                    }

                    else -> {
                        onMessageSend("Unknown command")
                    }
                }
                incrementSteps(user, onMessageSend)
            }
        } catch (e: IOException) {
            println("Connection problems with user " + user.username)
        } finally {
            user.setIsAlive(false)
            lock.withLock {
                val aliveUsersCount = room.users.count { it.isAlive }
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
        val game = Game(Screen(30, 30), 150, 150)
        return Room(roomId, roomId.toString(), user, game, onMessageSend)
    }

    private fun sendInstructions(user: User, onMessageSend: (String) -> Unit) {
        onMessageSend("You need to keep a space garbage.")
        onMessageSend("Your ID is " + user.id)
        onMessageSend("Good luck, Commander!")
    }

    private fun incrementSteps(user: User, onMessageSend: (String) -> Unit) {
        user.steps += 1
        if (user.steps >= 1500) {
            died(user, onMessageSend)
        }
        if (user.score < 0) {
            died(user, onMessageSend)
        }
    }

    private fun died(user: User, onMessageSend: (String) -> Unit) {
        user.setIsAlive(false)
        onMessageSend("died")
        val scoreMessage = String.format("You have collected %d score", user.score)
        onMessageSend(scoreMessage)
    }
}
