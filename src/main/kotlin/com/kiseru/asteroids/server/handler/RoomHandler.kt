package com.kiseru.asteroids.server.handler

interface RoomHandler {

    fun awaitStart()

    fun awaitFinish()

    fun sendMessage(message: String)

    fun handle()
}
