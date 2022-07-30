package com.kiseru.asteroids.server

import kotlinx.coroutines.runBlocking

private const val PORT = 6501

fun main() = runBlocking {
    val server = Server(PORT)
    server.up()
}
