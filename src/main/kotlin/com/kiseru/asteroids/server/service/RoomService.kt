package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.Room

interface RoomService {

    val rooms: List<Room>

    fun getNotFullRoom(): Room

    fun getRoomRating(room: Room): String

    fun sendMessageToUsers(room: Room, message: String)
}
