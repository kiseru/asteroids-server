package com.kiseru.asteroids.server

import com.kiseru.asteroids.server.impl.ConnectionReceiverImpl
import com.kiseru.asteroids.server.service.RoomService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.ServerSocket
import java.util.Scanner

class Server(
    private val roomService: RoomService,
    private val port: Int,
) {

    fun up() {
        startHandlingConnections()
        handleSystemCommands()
    }

    private fun startHandlingConnections() {
        val serverSocket = ServerSocket(port)
        val connectionReceiver = ConnectionReceiverImpl(serverSocket, roomService)
        runBlocking {
            launch(Dispatchers.IO) { connectionReceiver.acceptConnections() }
        }
    }

    private fun handleSystemCommands() =
        systemCommands()
            .forEach(::handleSystemCommand)

    private fun systemCommands(): Sequence<String> =
        sequence {
            val scanner = Scanner(System.`in`)
            while (true) {
                val command = scanner.nextLine()
                yield(command)
            }
        }

    private fun handleSystemCommand(command: String): Unit =
        when(command) {
            "RATING" -> roomService.writeRatings(System.out)
            "GAME_FIELD" -> roomService.writeGameFields(System.out)
            else -> println("Unknown command")
        }
}
