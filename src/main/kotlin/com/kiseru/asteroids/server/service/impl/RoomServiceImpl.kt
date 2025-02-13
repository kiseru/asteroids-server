package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.room.RoomStatus
import com.kiseru.asteroids.server.service.RoomService
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

class RoomServiceImpl : RoomService {

    private val rooms = mutableListOf<Room>()

    override fun writeRatings(outputStream: OutputStream): Unit =
        synchronized(this) {
            for (room in rooms) {
                writeRating(room, outputStream)
            }
        }

    private fun writeRating(room: Room, outputStream: OutputStream): Unit =
        try {
            val rating = getRoomRating(room)
            outputStream.write("$rating\n".toByteArray())
        } catch (_: IOException) {
            println("Failed to write the room's rating")
        }

    override fun writeGameFields(outputStream: OutputStream): Unit =
        synchronized(this) {
            for (room in rooms) {
                writeGameField(room, outputStream)
            }
        }

    override fun writeGameField(room: Room, outputStream: OutputStream): Unit =
        try {
            val screen = room.game.screen
            val gameField = screen.display()
            outputStream.write("$gameField\n".toByteArray())
        } catch (_: IOException) {
            println("Failed to write the room's game field")
        }

    override fun writeGameField(room: Room, onMessageSend: (String) -> Unit): Unit =
        try {
            val screen = room.game.screen
            val gameField = screen.display()
            onMessageSend("$gameField\n")
        } catch (_: IOException) {
            println("Failed to write the room's game field")
        }

    override fun createRoomHandler(lock: Lock, condition: Condition): (Room) -> Unit {
        return { room -> handleRoom(lock, condition, room) }
    }

    private fun handleRoom(lock: Lock, condition: Condition, room: Room) {
        lock.withLock {
            while (room.status != RoomStatus.FINISHED) {
                condition.await()
            }
        }

        for (handler in room.getSendMessageHandlers()) {
            handler("finish")
        }

        val rating = getRoomRating(room)
        for (handler in room.getSendMessageHandlers()) {
            handler(rating)
        }

        println("Room released!")
        println(rating)
        println()
    }

    override fun getRoomRating(room: Room): String =
        room.getUsers()
            .sortedByDescending { it.score }
            .joinToString("\n") { it.toString() }
}
