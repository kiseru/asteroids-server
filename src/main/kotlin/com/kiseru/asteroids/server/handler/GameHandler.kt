package com.kiseru.asteroids.server.handler

interface GameHandler {

    fun awaitStart()

    fun awaitFinish()

    fun sendMessage(message: String)

    fun handle()
}
