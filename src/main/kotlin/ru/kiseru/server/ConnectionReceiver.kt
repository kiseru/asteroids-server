package ru.kiseru.server

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.util.concurrent.atomic.AtomicLong
import ru.kiseru.logics.User
import ru.kiseru.server.room.Room

class ConnectionReceiver(private val connectionReceiver: ServerSocket) {

    companion object {
        private val ID_GENERATOR = AtomicLong()
    }

    fun handle() {
        while (true) {
            try {
                val newConnection = connectionReceiver.accept()
                val notFullRoom: Room = Server.getNotFullRoom()
                val reader = BufferedReader(InputStreamReader(newConnection.inputStream))
                val writer = PrintWriter(newConnection.outputStream, true)
                writer.println("Welcome To Asteroids Server")
                writer.println("Please, introduce yourself!")
                val user = User(ID_GENERATOR.incrementAndGet(), reader.readLine())
                val userHandler = UserHandler(reader, writer, notFullRoom, user)
                Thread(userHandler::run).start()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }
}
