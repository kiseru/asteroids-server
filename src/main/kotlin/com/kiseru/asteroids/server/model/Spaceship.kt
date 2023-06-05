package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.exception.GameFinishedException
import com.kiseru.asteroids.server.service.CourseCheckerService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class Spaceship(
    val id: UUID,
    private val owner: User,
    private val room: Room,
    private val courseCheckerService: CourseCheckerService,
    x: Int,
    y: Int,
) : Point(x, y) {

    override val symbolToShow: String
        get() = owner.id.toString()

    override val type: Type = Type.SPACESHIP

    val isAsteroidInFrontOf
        get() = courseCheckerService.isAsteroid()

    val isGarbageInFrontOf
        get() = courseCheckerService.isGarbage()

    val isWallInFrontOf
        get() = courseCheckerService.isWall()

    var direction = Direction.UP

    private val mutex = Mutex()

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
    suspend fun crash(type: Type) {
        mutex.withLock {
            if (type === Type.ASTEROID) {
                subtractScore()
            } else if (type === Type.GARBAGE) {
                addScore()
                val collected = room.game.incrementCollectedGarbageCount()
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
        if (room.isGameFinished) {
            throw GameFinishedException()
        }

        owner.score -= 50
        if (owner.score < 0) {
            owner.isAlive = false
        }
    }

    private fun addScore() {
        if (room.isGameFinished) {
            throw GameFinishedException()
        }

        owner.score += 10
    }

    fun isWall(screen: Screen) = direction.isWall(this, screen)

    fun checkContaining(coordinates: List<Point>): Boolean = direction.checkContaining(this, coordinates)

    private fun checkCollectedGarbage(collected: Int) {
        if (collected >= room.game.garbageNumber) {
            room.setGameFinished()
        }
    }
}
