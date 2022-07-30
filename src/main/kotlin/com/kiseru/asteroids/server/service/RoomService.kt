package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.Room
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object RoomService {

    val rooms = mutableListOf<Room>()

    private var notFullRoom = Room()

    private val lock = ReentrantLock()

    fun getNotFullRoom(): Room {
        lock.withLock {
            if (!notFullRoom.isFull()) {
                return notFullRoom
            }

            rooms.add(notFullRoom)
            notFullRoom = Room()
            return notFullRoom
        }
    }

    /**
     * Возвращает рейтинг пользователей комнаты.
     *
     * @return рейтинг пользователей комнаты
     */
    fun getRoomRating(room: Room): String {
        return room.users.sortedBy { it.score }
            .joinToString("\n") { "${it.username} ${it.score}" }
    }

    /**
     * Рассылает сообщение пользователям комнаты.
     *
     * @param message сообщение
     */
    fun sendMessageToUsers(room: Room, message: String) {
        for (user in room.users) {
            user.sendMessage(message)
        }
    }
}