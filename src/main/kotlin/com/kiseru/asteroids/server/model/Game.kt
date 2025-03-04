package com.kiseru.asteroids.server.model

import java.util.Random
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

class Game(
    val id: UUID,
    val name: String,
    val size: Int,
    val screen: Screen,
    val garbageNumber: Int,
) {

    val gameObjects = mutableListOf<GameObject>()
    var status = GameStatus.CREATED

    private val collectedGarbageCount = AtomicInteger(0)
    private val spaceships = mutableListOf<Spaceship>()
    private val sendMessageHandlers = mutableListOf<(String) -> Unit>()

    fun refresh() {
        screen.update()
        gameObjects.forEach(screen::render)
    }

    fun generateUniqueRandomCoordinates(): Pair<Int, Int> {
        val random = Random()
        var randomCoordinates: Pair<Int, Int>? = null
        while (randomCoordinates == null || isGameObjectsContainsCoordinates(randomCoordinates)) {
            randomCoordinates = random.nextInt(screen.width) + 1 to random.nextInt(screen.height) + 1
        }

        return randomCoordinates
    }

    private fun isGameObjectsContainsCoordinates(coordinates: Pair<Int, Int>): Boolean =
        gameObjects.any { it.coordinates == coordinates }

    fun addPoint(gameObject: GameObject) {
        gameObjects.add(gameObject)
    }

    fun incrementCollectedGarbageCount(): Int =
        collectedGarbageCount.incrementAndGet()

    fun isAsteroidAhead(spaceship: Spaceship): Boolean =
        gameObjects.any { it.type == Type.ASTEROID && isPointAhead(spaceship, it) }

    fun isGarbageAhead(spaceship: Spaceship): Boolean =
        gameObjects.any { it.type == Type.GARBAGE && isPointAhead(spaceship, it) }

    private fun isPointAhead(spaceship: Spaceship, gameObject: GameObject): Boolean =
        when (spaceship.direction) {
            Direction.UP -> spaceship.x == gameObject.x && spaceship.y == gameObject.y + 1
            Direction.DOWN -> spaceship.x == gameObject.x && spaceship.y == gameObject.y - 1
            Direction.LEFT -> spaceship.x == gameObject.x + 1 && spaceship.y == gameObject.y
            Direction.RIGHT -> spaceship.x == gameObject.x - 1 && spaceship.y == gameObject.y
        }

    fun isWallAhead(spaceship: Spaceship): Boolean =
        when (spaceship.direction) {
            Direction.UP -> spaceship.y == 1
            Direction.DOWN -> spaceship.y == screen.height
            Direction.LEFT -> spaceship.x == 1
            Direction.RIGHT -> spaceship.x == screen.width
        }

    fun addSpaceship(spaceship: Spaceship, onMessageSend: (String) -> Unit) {
        spaceships.add(spaceship)
        sendMessageHandlers.add(onMessageSend)
    }

    fun getSpaceships(): List<Spaceship> =
        spaceships

    fun getSendMessageHandlers(): List<(String) -> Unit> =
        sendMessageHandlers
}