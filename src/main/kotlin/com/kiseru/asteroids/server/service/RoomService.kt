package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.room.Room
import java.io.OutputStream
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock

interface RoomService {

    fun writeRatings(outputStream: OutputStream)

    fun writeGameFields(outputStream: OutputStream)

    fun writeGameField(room: Room, outputStream: OutputStream)

    fun writeGameField(room: Room, onMessageSend: (String) -> Unit)

    fun createRoomHandler(lock: Lock, condition: Condition): (Room) -> Unit

    fun getRoomRating(room: Room): String
}
