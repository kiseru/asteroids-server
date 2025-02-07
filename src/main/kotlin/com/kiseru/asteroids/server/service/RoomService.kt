package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.room.Room
import java.io.OutputStream
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock

interface RoomService {

    val notFullRoomLock: Lock

    val notFullRoomCondition: Condition

    fun writeRatings(outputStream: OutputStream)

    fun writeGameFields(outputStream: OutputStream)

    fun writeGameField(room: Room, outputStream: OutputStream)

    fun getNotFullRoom(): Room
}
