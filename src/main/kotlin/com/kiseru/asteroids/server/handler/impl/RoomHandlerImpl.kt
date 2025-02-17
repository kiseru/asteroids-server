package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.handler.RoomHandler
import com.kiseru.asteroids.server.logics.CourseChecker
import com.kiseru.asteroids.server.logics.models.Spaceship
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
            registerSpaceshipForUser(roomUser)
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

    private fun registerSpaceshipForUser(user: User) {
        val coordinates = room.game.generateUniqueRandomCoordinates()
        val courseChecker = CourseChecker(room.game.pointsOnScreen, room.game.screen)
        val spaceship = Spaceship(coordinates, user, courseChecker, lock, condition)
        user.spaceship = spaceship
        room.game.addPoint(spaceship)
        room.game.addCrashHandler {
            room.game.check(room.game, spaceship, room.status) { roomStatus -> room.status = roomStatus }
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
