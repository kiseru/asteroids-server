package com.kiseru.asteroids.server.impl

import com.kiseru.asteroids.server.ConnectionReceiver
import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.logics.auxiliary.Direction
import com.kiseru.asteroids.server.room.Room
import com.kiseru.asteroids.server.room.RoomStatus
import com.kiseru.asteroids.server.service.RoomService
import java.io.BufferedReader
import java.io.IOException
import java.io.OutputStream
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
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
            val notFullRoom = roomService.getNotFullRoom()
            val notFullRoomLock = roomService.notFullRoomLock
            val notFullRoomCondition = roomService.notFullRoomCondition
            val inputStream = socket.getInputStream()
            val outputStream = socket.getOutputStream()
            val printWriter = PrintWriter(outputStream, true)
            val reader = inputStream.bufferedReader()
            val onMessageSend: (String) -> Unit = printWriter::println
            val user = User()
            thread { handleUser(printWriter, reader, user, onMessageSend, notFullRoom, outputStream, notFullRoomLock, notFullRoomCondition) }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun handleUser(
        writer: PrintWriter,
        reader: BufferedReader,
        user: User,
        onMessageSend: (String) -> Unit,
        room: Room,
        outputStream: OutputStream,
        lock: Lock,
        condition: Condition,
    ) {
        try {
            writer.println("Welcome To Asteroids Server")
            writer.println("Please, introduce yourself!")
            val username = reader.readLine()
            user.username = username
            println("$username has joined the server!")
            writer.println("You need to keep a space garbage.")
            writer.println("Your ID is " + user.id)
            writer.println("Good luck, Commander!")
            lock.withLock {
                room.waitStart(user, onMessageSend, roomService::getNotFullRoom, lock, condition)
            }
            user.spaceship.direction = Direction.UP
            while (room.status != RoomStatus.FINISHED && user.isAlive) {
                val userMessage = reader.readLine()
                when (userMessage) {
                    "go" -> {
                        user.spaceship.go()
                        room.game.refresh()
                        writer.println(user.score.toString())
                    }
                    "left" -> {
                        user.spaceship.direction = Direction.LEFT
                        room.game.refresh()
                        writer.println("success")
                    }
                    "right" -> {
                        user.spaceship.direction = Direction.RIGHT
                        room.game.refresh()
                        writer.println("success")
                    }
                    "up" -> {
                        user.spaceship.direction = Direction.UP
                        room.game.refresh()
                        writer.println("success")
                    }
                    "down" -> {
                        user.spaceship.direction = Direction.DOWN
                        room.game.refresh()
                        writer.println("success")
                    }
                    "isAsteroid" -> {
                        writer.println(if (user.spaceship.courseChecker.isAsteroid) "t" else "f")
                    }
                    "isGarbage" -> {
                        writer.println(if (user.spaceship.courseChecker.isGarbage) "t" else "f")
                    }
                    "isWall" -> {
                        writer.println(if (user.spaceship.courseChecker.isWall) "t" else "f")
                    }
                    "GAME_FIELD" -> {
                        roomService.writeGameField(room, outputStream)
                    }
                    else -> {
                        writer.println("Unknown command")
                    }
                }
                incrementSteps(user, writer)
            }
        } catch (e: IOException) {
            println("Connection problems with user " + user.username)
        } finally {
            user.setIsAlive(false)
            lock.withLock {
                if (room.aliveCount() == 0L) {
                    room.status = RoomStatus.FINISHED
                }
                condition.signalAll()
            }
        }
    }

    private fun incrementSteps(user: User, writer: PrintWriter) {
        user.steps += 1
        if (user.steps >= 1500) {
            died(user, writer)
        }
        if (user.score < 0) {
            died(user, writer)
        }
    }

    private fun died(user: User, writer: PrintWriter) {
        user.setIsAlive(false)
        writer.println("died")
        val scoreMessage = String.format("You have collected %d score", user.score)
        writer.println(scoreMessage)
    }
}
