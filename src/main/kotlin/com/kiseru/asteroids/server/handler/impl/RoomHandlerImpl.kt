package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.handler.RoomHandler
import com.kiseru.asteroids.server.logics.CourseChecker
import com.kiseru.asteroids.server.logics.Game
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates
import com.kiseru.asteroids.server.logics.auxiliary.Direction
import com.kiseru.asteroids.server.logics.auxiliary.Type
import com.kiseru.asteroids.server.logics.models.Spaceship
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.RoomStatus
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
        val spaceship = Spaceship(coordinates, user.id, courseChecker)
        user.spaceship = spaceship
        room.game.addPoint(spaceship)
        room.game.addCrashHandler { checkSpaceship(spaceship) }
    }

    private fun checkSpaceship(spaceship: Spaceship) {
        val collisionPoint = room.game.pointsOnScreen.firstOrNull {
            it.type != Type.SPACESHIP && it.isVisible && it.coordinates == spaceship.coordinates
        }

        if (collisionPoint != null) {
            lock.withLock {
                onSpaceshipDestroy(spaceship, collisionPoint.type)
                condition.signalAll()
            }
            collisionPoint.destroy()
        } else if (spaceship.x == 0
            || spaceship.y == 0
            || spaceship.x > room.game.screen.width
            || spaceship.y > room.game.screen.height
        ) {
            lock.withLock {
                onSpaceshipDestroy(spaceship, Type.WALL)
                condition.signalAll()
            }
        }
    }

    private fun onSpaceshipDestroy(spaceship: Spaceship, type: Type) {
        if (type == Type.ASTEROID) {
            if (room.status == RoomStatus.GAMING) {
                spaceship.subtractScore()
            }
        } else if (type == Type.GARBAGE) {
            if (room.status == RoomStatus.GAMING) {
                spaceship.addScore()
            }
            checkCollectedGarbage(room.game)
        } else if (type == Type.WALL) {
            // возвращаемся назад, чтобы не находится на стене
            rollbackLastStep(spaceship.direction, spaceship)
            if (room.status == RoomStatus.GAMING) {
                spaceship.subtractScore()
            }
        }
        if (!spaceship.isAlive) {
            spaceship.destroy()
        }
    }

    private fun checkCollectedGarbage(game: Game) {
        val collected = game.incrementCollectedGarbageCount()
        if (collected >= game.garbageNumber) {
            room.status = RoomStatus.FINISHED
        }
    }

    private fun rollbackLastStep(direction: Direction, spaceship: Spaceship) {
        when (direction) {
            Direction.UP -> spaceship.coordinates = Coordinates(spaceship.x, spaceship.y + 1)
            Direction.RIGHT -> spaceship.coordinates = Coordinates(spaceship.x - 1, spaceship.y)
            Direction.DOWN -> spaceship.coordinates = Coordinates(spaceship.x, spaceship.y - 1)
            Direction.LEFT -> spaceship.coordinates = Coordinates(spaceship.x + 1, spaceship.y)
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
