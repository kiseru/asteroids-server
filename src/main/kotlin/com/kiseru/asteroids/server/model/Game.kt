package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.handler.SpaceshipCrashHandler
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class Game(
    val id: UUID,
    val screen: Screen,
    val garbageNumber: Int,
    val pointsOnScreen: MutableList<Point>,
    val gameObjects: MutableList<Point>,
) {

    val crashHandlers = mutableListOf<SpaceshipCrashHandler>()

    private val collectedGarbageCountMutex = Mutex()

    private var collectedGarbageCount = 0

    suspend fun refresh() {
        screen.update()
        for (crashHandler in crashHandlers) {
            crashHandler.check()
        }

        for (gameObject in gameObjects) {
            gameObject.render(screen)
        }
    }

    suspend fun incrementCollectedGarbageCount(): Int = collectedGarbageCountMutex.withLock {
        collectedGarbageCount++
    }
}
