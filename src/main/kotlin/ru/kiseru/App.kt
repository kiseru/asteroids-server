package ru.kiseru

import ru.kiseru.server.Server

fun main() {
    val server = Server(6501)
    server.up()
}
