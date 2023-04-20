package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.exception.GameFinishedException
import com.kiseru.asteroids.server.service.CourseCheckerService
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Spaceship(
    private val owner: User,
    private val courseCheckerService: CourseCheckerService,
    x: Int,
    y: Int,
) : Point(x, y) {

    override val symbolToShow: String
        get() = owner.id

    override val type: Type = Type.SPACESHIP

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
    fun go() = direction.go(this)

    /**
     * Вызывается при выявлении столкновения корабля с чем-либо
     *
     * @param type: тип объекта, с которым произошло столкновение
     */
    fun crash(type: Type) {
        lock.withLock {
            if (type === Type.ASTEROID) {
                subtractScore()
            } else if (type === Type.GARBAGE) {
                addScore()
                val collected = owner.room.incrementCollectedGarbageCount()
                checkCollectedGarbage(collected)
            } else if (type === Type.WALL) {
                direction.rollback(this)
                subtractScore()
            }
            if (!owner.isAlive) {
                destroy()
            }
        }
    }

    private fun subtractScore() {
        if (owner.room.isGameFinished) {
            throw GameFinishedException()
        }

        owner.score -= 50
        if (owner.score < 0) {
            owner.isAlive = false
        }
    }

    private fun addScore() {
        if (owner.room.isGameFinished) {
            throw GameFinishedException()
        }

        owner.score += 10
    }

    fun isWall(screen: Screen) = direction.isWall(this, screen)

    fun checkContaining(coordinates: List<Point>): Boolean = direction.checkContaining(this, coordinates)

    private fun checkCollectedGarbage(collected: Int) {
        owner.room.checkCollectedGarbage(collected)
    }
}