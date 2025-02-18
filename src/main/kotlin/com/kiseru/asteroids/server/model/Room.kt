package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.logics.Game
import java.util.UUID

class Room(
    private val id: UUID,
    val name: String,
    val game: Game,
    val size: Int,
) {

    private val users = mutableListOf<User>()
    private val sendMessageHandlers = mutableListOf<(String) -> Unit>()

    var status = RoomStatus.WAITING_CONNECTIONS

    fun addUser(user: User, onMessageSend: (String) -> Unit) {
        users.add(user)
        sendMessageHandlers.add(onMessageSend)
    }

    fun getUsers(): List<User> =
        users.toMutableList()

    fun getSendMessageHandlers(): List<(String) -> Unit> =
        sendMessageHandlers
}
