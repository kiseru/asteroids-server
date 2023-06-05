package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.Room
import java.util.*

interface RoomService {

    suspend fun getNotFullRoom(): Room

    suspend fun sendMessageToUsers(room: Room, message: String)

    fun showAllRatings()

    fun showAllGameFields()

    suspend fun startRoom(room: Room)

    suspend fun awaitEndgame(room: Room)

    fun findRoomById(roomId: UUID): Room?
}
