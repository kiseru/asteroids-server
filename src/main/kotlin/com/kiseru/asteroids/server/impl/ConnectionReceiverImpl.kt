package com.kiseru.asteroids.server.impl

import com.kiseru.asteroids.server.ConnectionReceiver
import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.service.RoomService
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class ConnectionReceiverImpl(
    private val serverSocket: ServerSocket,
    private val roomService: RoomService,
) : ConnectionReceiver {

    override fun acceptConnections(): Unit =
        connections()
            .forEach(::handleConnection)

    private fun connections(): Sequence<Socket> =
        sequence {
            while (true) {
                val socket = serverSocket.accept()
                yield(socket)
            }
        }

    private fun handleConnection(socket: Socket): Unit =
        try {
            val notFullRoom = roomService.notFullRoom
            val user = User(socket, notFullRoom, roomService)
            Thread(user).start()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
}
