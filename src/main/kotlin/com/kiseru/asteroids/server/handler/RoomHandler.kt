package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.room.Room

interface RoomHandler {

    fun awaitStart()

    fun awaitFinish()

    fun sendMessage(message: String)

    fun handle(room: Room)
}
