package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.service.impl.GameServiceImpl

suspend fun main() {
    val roomService = GameServiceImpl()
    val server = Server(roomService, 6501)
    server.up()
}
