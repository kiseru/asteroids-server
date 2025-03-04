package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.RoomHandler
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Type
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.RoomStatus
import com.kiseru.asteroids.server.model.Spaceship
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
        room.status = RoomStatus.GAMING
        lock.withLock { condition.signalAll() }
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
            while (room.getSpaceships().size < room.size) {
                condition.await()
            }
        }
    }

    fun checkSpaceship(spaceship: Spaceship) {
        val collisionPoint = room.game.gameObjects.firstOrNull {
            it.type != Type.SPACESHIP && it.isVisible && it.coordinates == spaceship.coordinates
        }

        if (collisionPoint != null) {
            lock.withLock {
                onSpaceshipDestroy(spaceship, collisionPoint.type)
                condition.signalAll()
            }
            collisionPoint.destroy()
            room.game.gameObjects.remove(collisionPoint)
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
        spaceship.coordinates = when (direction) {
            Direction.UP -> spaceship.x to spaceship.y + 1
            Direction.RIGHT -> spaceship.x - 1 to spaceship.y
            Direction.DOWN -> spaceship.x to spaceship.y - 1
            Direction.LEFT -> spaceship.x + 1 to spaceship.y
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
