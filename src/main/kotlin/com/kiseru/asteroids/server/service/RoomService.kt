package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.room.Room
import java.io.OutputStream

interface RoomService {

    fun writeRatings(outputStream: OutputStream)

    fun writeGameFields(outputStream: OutputStream)

    fun writeGameField(room: Room, outputStream: OutputStream)

    fun getNotFullRoom(): Room
}
