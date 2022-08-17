package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.service.CourseCheckerService
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Spaceship(
    private val owner: User,
    private val courseCheckerService: CourseCheckerService,
    coordinates: Coordinates,
) : Point(coordinates) {

    override val symbolToShow: String
        get() = owner.id.toString()

    override val type: Type
        get() = Type.SPACESHIP

    val isAsteroidInFrontOf
        get() = courseCheckerService.isAsteroid()

    val isGarbageInFrontOf
        get() = courseCheckerService.isGarbage()

    val isWallInFrontOf
        get() = courseCheckerService.isWall()

    var direction = Direction.UP

    private val lock: Lock = ReentrantLock()

    override fun destroy() {
        throw UnsupportedOperationException()
    }

    /**
     * Делает шаг в текущем направлении.
     */
    fun go() {
        coordinates = when (direction) {
            Direction.UP -> Coordinates(x, y - 1)
            Direction.RIGHT -> Coordinates(x + 1, y)
            Direction.DOWN -> Coordinates(x, y + 1)
            Direction.LEFT -> Coordinates(x - 1, y)
        }
    }

    /**
     * Вызывается при выявлении столкновения корабля с чем-либо
     *
     * @param type: тип объекта, с которым произошло столкновение
     */
    fun crash(type: Type) {
        lock.withLock {
            if (type === Type.ASTEROID) {
                owner.subtractScore()
            } else if (type === Type.GARBAGE) {
                owner.addScore()
                val collected = owner.room.incrementCollectedGarbageCount()
                checkCollectedGarbage(collected)
            } else if (type === Type.WALL) {
                // возвращаемся назад, чтобы не находится на стене
                rollbackLastStep()
                owner.subtractScore()
            }
            if (!owner.isAlive) {
                destroy()
            }
        }
    }

    private fun checkCollectedGarbage(collected: Int) {
        owner.checkCollectedGarbage(collected)
    }

    private fun rollbackLastStep() {
        coordinates = when (direction) {
            Direction.UP -> Coordinates(x, y + 1)
            Direction.RIGHT -> Coordinates(x - 1, y)
            Direction.DOWN -> Coordinates(x, y - 1)
            Direction.LEFT -> Coordinates(x + 1, y)
        }
    }
}