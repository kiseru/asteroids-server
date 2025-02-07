package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.room.Room
import com.kiseru.asteroids.server.room.RoomStatus
import com.kiseru.asteroids.server.service.RoomService
import java.io.IOException
import java.io.OutputStream

class RoomServiceImpl : RoomService {

    private val rooms = mutableListOf<Room>()

    private var notFullRoom = Room(::getNotFullRoom, ::handleRoom)

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
                notFullRoom = Room(::getNotFullRoom, ::handleRoom)
            }

            notFullRoom
        }

    private fun handleRoom(room: Room, users: List<User?>) {
        for (user in users) {
            user?.sendMessage("start")
        }

        room.setStatus(RoomStatus.GAMING)
        room.createGame()
        room.waitFinish()

        for (user in users) {
            user?.sendMessage("finish")
        }

        val rating = room.rating
        for (user in users) {
            user?.sendMessage(rating)
        }

        println("Room released!")
        println(rating)
        println()
    }
}
