package com.kiseru.asteroids.server.model

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
    fun go() = direction.go(this)

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
                direction.rollback(this)
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
}