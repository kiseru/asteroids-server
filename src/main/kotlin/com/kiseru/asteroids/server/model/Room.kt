package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.Server
import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.logics.Game
import com.kiseru.asteroids.server.logics.Screen
import com.kiseru.asteroids.server.room.RoomStatus

class Room1 : Thread() {

    private val users = mutableListOf<User>()

    private var status = RoomStatus.WAITING_CONNECTIONS

    lateinit var game: Game
        private set

    @Synchronized
    fun addUser(user: User) {
        if (users.size >= MAX_USERS) {
            Server.getNotFullRoom().addUser(user)
        }

        for (roomUser in users) {
            roomUser.sendMessage("User ${user.userName} has joined the room.")
        }

        users.add(user)
    }

    @Synchronized
    fun removeUser(user: User) {
        if (users.size == 0) {
            return
        }

        users.remove(user)
    }

    fun getRating(): String {
        users.sortBy { it.score }
        return users.joinToString("\n") { it.toString() }
    }

    fun aliveCount() = users.count { it.isAlive() }

    fun isFull() = users.size >= MAX_USERS

    fun isGameStarted() = status == RoomStatus.GAMING

    fun isGameFinished() = status == RoomStatus.FINISHED

    override fun run() {
        for (user in users) {
            user.sendMessage("start")
        }

        status = RoomStatus.GAMING
        synchronized(Server::class.java) {
            game = Game(Screen(30, 30), 150, 50)
            for (user in users) {
                game.registerSpaceShipForUser(user)
            }
        }

        synchronized(this) {
            game.refresh()
            status = RoomStatus.FINISHED
        }

        for (user in users) {
            user.sendMessage("finish")
            user.sendMessage(getRating())
        }

        println("Room released!")
        println(getRating())
        println()
    }

    companion object {
        private const val MAX_USERS = 1
    }
}