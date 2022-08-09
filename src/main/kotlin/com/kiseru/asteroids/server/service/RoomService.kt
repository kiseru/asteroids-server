package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.Room

interface RoomService {

    fun getNotFullRoom(): Room

    fun getRoomRating(room: Room): String

    fun sendMessageToUsers(room: Room, message: String)

    fun showAllRatings()

    fun showAllGameFields()
}
