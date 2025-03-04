package com.kiseru.asteroids.server.model

import java.util.UUID

class Room(
    private val id: UUID,
    val name: String,
    val game: Game,
    val size: Int,
) {

    private val spaceships = mutableListOf<Spaceship>()
    private val sendMessageHandlers = mutableListOf<(String) -> Unit>()

    var status = RoomStatus.WAITING_CONNECTIONS

    fun addUser(spaceship: Spaceship, onMessageSend: (String) -> Unit) {
        spaceships.add(spaceship)
        sendMessageHandlers.add(onMessageSend)
    }

    fun getSpaceships(): List<Spaceship> =
        spaceships.toList()

    fun getSendMessageHandlers(): List<(String) -> Unit> =
        sendMessageHandlers
}
