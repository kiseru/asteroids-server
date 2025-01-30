package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.service.impl.RoomServiceImpl

fun main() {
    val roomService = RoomServiceImpl()
    val server = Server(roomService, 6501)
    server.up()
}
