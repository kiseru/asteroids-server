package com.kiseru.asteroids.server.model

import java.util.Random
import java.util.UUID

class Game(
    val id: UUID,
    val name: String,
    val size: Int,
    var fieldWidth: Int,
    var fieldHeight: Int,
    private var garbageNumber: Int,
) {

    val gameObjects = mutableListOf<GameObject>()
    var status = GameStatus.CREATED

    private val spaceships = mutableListOf<Spaceship>()
    private val sendMessageHandlers = mutableListOf<(String) -> Unit>()

    fun generateUniqueRandomCoordinates(): Pair<Int, Int> {
        val random = Random()
        var randomCoordinates: Pair<Int, Int>? = null
        while (randomCoordinates == null || isGameObjectsContainsCoordinates(randomCoordinates)) {
            randomCoordinates = random.nextInt(fieldWidth) + 1 to random.nextInt(fieldHeight) + 1
        }

        return randomCoordinates
    }

    private fun isGameObjectsContainsCoordinates(coordinates: Pair<Int, Int>): Boolean =
        gameObjects.any { it.coordinates == coordinates }

    fun addPoint(gameObject: GameObject) {
        gameObjects.add(gameObject)
    }

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
            Direction.DOWN -> spaceship.y == fieldHeight
            Direction.LEFT -> spaceship.x == 1
            Direction.RIGHT -> spaceship.x == fieldWidth
        }

    fun addSpaceship(spaceship: Spaceship, onMessageSend: (String) -> Unit) {
        spaceships.add(spaceship)
        sendMessageHandlers.add(onMessageSend)
    }

    fun getSpaceships(): List<Spaceship> =
        spaceships

    fun getSendMessageHandlers(): List<(String) -> Unit> =
        sendMessageHandlers

    fun damageSpaceship(spaceship: Spaceship, type: Type) {
        if (status != GameStatus.STARTED) {
            throw IllegalStateException("Game must have STARTED status")
        }

        when (type) {
            Type.ASTEROID -> {
                spaceship.subtractScore()
            }
            Type.GARBAGE -> {
                spaceship.addScore()
                onGarbageCollected()
            }
            Type.WALL -> {
                rollback(spaceship)
                spaceship.subtractScore()
            }
            Type.SPACESHIP -> {
                rollback(spaceship)
                spaceship.subtractScore()
            }
        }

        if (!spaceship.isAlive) {
            spaceship.destroy()
        }
    }

    private fun onGarbageCollected() {
        garbageNumber--
        if (garbageNumber == 0) {
            status = GameStatus.FINISHED
        }
    }

    private fun rollback(spaceship: Spaceship) {
        spaceship.coordinates = when (spaceship.direction) {
            Direction.UP -> spaceship.x to spaceship.y + 1
            Direction.RIGHT -> spaceship.x - 1 to spaceship.y
            Direction.DOWN -> spaceship.x to spaceship.y - 1
            Direction.LEFT -> spaceship.x + 1 to spaceship.y
        }
    }

    fun freeCoordinates(): Sequence<Pair<Int, Int>> =
        sequence {
            while (true) {
                yield(generateUniqueRandomCoordinates())
            }
        }
}