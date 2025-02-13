package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.RoomHandler
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.room.RoomStatus
import com.kiseru.asteroids.server.service.RoomService
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

class RoomHandlerImpl(
    private val room: Room,
    private val lock: Lock,
    private val condition: Condition,
    private val roomService: RoomService,
) : RoomHandler {

    override fun handle() {
        awaitStart()
        for (roomUser in room.getUsers()) {
            room.game.registerSpaceshipForUser(roomUser, lock, condition) {
                room.game.check(room.game, it, room.status) { roomStatus -> room.status = roomStatus }
            }
        }
        room.status = RoomStatus.GAMING
        lock.withLock { condition.signalAll() }
        room.game.refresh()
        sendMessage("start")
        awaitFinish()
        sendMessage("finish")
        val rating = roomService.getRoomRating(room)
        sendMessage(rating)
        println("Room released!")
        println(rating)
        println()
    }

    override fun awaitStart() {
        lock.withLock {
            while (room.getUsers().size < room.size) {
                condition.await()
            }
        }
    }

    override fun awaitFinish() {
        lock.withLock {
            while (room.status != RoomStatus.FINISHED) {
                condition.await()
            }
        }
    }

    override fun sendMessage(message: String) {
        for (handler in room.getSendMessageHandlers()) {
            handler(message)
        }
    }
}
