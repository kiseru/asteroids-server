package ru.kiseru.server

import java.net.ServerSocket
import java.util.Scanner
import ru.kiseru.server.room.Room

class Server(private val port: Int) {

    fun up() {
        val server = ServerSocket(port)
        val connectionReceiver = ConnectionReceiver(server)
        Thread(connectionReceiver::handle).start()
        val sc = Scanner(System.`in`)
        while (true) {
            when (sc.nextLine()) {
                "rating" -> rooms.forEach { println(it.rating) }
                "gamefield" -> rooms.forEach { println(it.game.screen.display()) }
            }
        }
    }

    companion object {
        private val rooms: MutableList<Room> = mutableListOf()
        private var notFullRoom = Room()

        @Synchronized
        fun getNotFullRoom(): Room {
            if (!notFullRoom.isFull) return notFullRoom

            rooms.add(notFullRoom)
            notFullRoom = Room()
            return notFullRoom
        }
    }
}
