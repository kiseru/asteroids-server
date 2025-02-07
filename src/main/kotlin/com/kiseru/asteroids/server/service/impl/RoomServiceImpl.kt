package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.room.Room
import com.kiseru.asteroids.server.room.RoomStatus
import com.kiseru.asteroids.server.service.RoomService
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class RoomServiceImpl : RoomService {

    private val rooms = mutableListOf<Room>()

    override var notFullRoomLock: Lock = ReentrantLock()
        private set
    override var notFullRoomCondition: Condition = notFullRoomLock.newCondition()
        private set
    private var notFullRoom = Room(
        ::getNotFullRoom,
        createRoomHandler(notFullRoomLock, notFullRoomCondition)
    )

    override fun writeRatings(outputStream: OutputStream): Unit =
        synchronized(this) {
            for (room in rooms) {
                writeRating(room, outputStream)
            }
        }

    private fun writeRating(room: Room, outputStream: OutputStream): Unit =
        try {
            val rating = room.rating
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

    override fun getNotFullRoom(): Room =
        synchronized(this) {
            if (notFullRoom.isFull) {
                rooms.add(notFullRoom)
                notFullRoomLock = ReentrantLock()
                notFullRoomCondition = notFullRoomLock.newCondition()
                val roomHandler = createRoomHandler(notFullRoomLock, notFullRoomCondition)
                val room = Room(::getNotFullRoom, roomHandler)
                notFullRoom = room
            }

            notFullRoom
        }

    private fun createRoomHandler(lock: Lock, condition: Condition): (Room) -> Unit {
        return { room -> handleRoom(lock, condition, room) }
    }

    private fun handleRoom(lock: Lock, condition: Condition, room: Room) {
        lock.withLock {
            while (room.status != RoomStatus.FINISHED) {
                condition.await()
            }
        }

        for (handler in room.onMessageSendHandlers) {
            handler.accept("finish")
        }

        val rating = room.rating
        for (handler in room.onMessageSendHandlers) {
            handler.accept(rating)
        }

        println("Room released!")
        println(rating)
        println()
    }

}
