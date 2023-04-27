package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.SpaceshipCrashHandler
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Point
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.Type

class SpaceshipCrashHandlerImpl(
    private val game: Game,
    private val spaceship: Spaceship,
) : SpaceshipCrashHandler {

    override suspend fun check() {
        val pointsOnScreen = game.pointsOnScreen
        val collisionPoint = pointsOnScreen.firstOrNull {
            it.type != Type.SPACESHIP && it.isVisible && it.x == spaceship.x && it.y == spaceship.y
        }
        checkSpaceshipCrashing(collisionPoint)
    }

    private suspend fun checkSpaceshipCrashing(collisionPoint: Point?) {
        if (collisionPoint != null) {
            spaceship.crash(collisionPoint.type)
            collisionPoint.destroy()
        } else if (spaceship.x !in 1..game.screen.width || spaceship.y !in 1..game.screen.height) {
            spaceship.crash(Type.WALL)
        }
    }
}