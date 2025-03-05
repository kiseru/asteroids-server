package com.kiseru.asteroids.server.model

import java.util.Random
import java.util.UUID

class Game(
    val id: UUID,
    val name: String,
    val size: Int,
    var fieldWidth: Int,
    var fieldHeight: Int,
) {

    val gameObjects = mutableListOf<GameObject>()
    var status = GameStatus.CREATED

    private val spaceships = mutableListOf<Spaceship>()
    private val sendMessageHandlers = mutableListOf<(String) -> Unit>()

    private fun generateUniqueRandomCoordinates(): Pair<Int, Int> {
        if (gameObjects.size == fieldWidth * fieldHeight) {
            throw IllegalStateException()
        }

        return sequence {
            val random = Random()
            while (true) {
                yield(random.nextInt(fieldWidth) + 1 to random.nextInt(fieldHeight) + 1)
            }
        }
            .filterNot { (x, y) -> isGameObjectsContainsCoordinates(x, y) }
            .first()
    }

    private fun isGameObjectsContainsCoordinates(x: Int, y: Int): Boolean =
        gameObjects.any { it.x == x && it.y == y }

    fun addGameObject(gameObject: GameObject) {
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
        val isGarbageExists = gameObjects.any { it.type == Type.GARBAGE }
        if (!isGarbageExists) {
            status = GameStatus.FINISHED
        }
    }

    private fun rollback(spaceship: Spaceship) {
        when (spaceship.direction) {
            Direction.UP -> spaceship.y += 1
            Direction.RIGHT -> spaceship.x -= 1
            Direction.DOWN -> spaceship.y -= 1
            Direction.LEFT -> spaceship.x += 1
        }
    }

    fun freeCoordinates(): Sequence<Pair<Int, Int>> =
        sequence {
            while (true) {
                yield(generateUniqueRandomCoordinates())
            }
        }

    fun onSpaceshipMove(spaceship: Spaceship) {
        val isBusy = when (spaceship.direction) {
            Direction.UP -> spaceships.any { it.x == spaceship.x && it.y == spaceship.y - 1 }
            Direction.DOWN -> spaceships.any { it.x == spaceship.x && it.y == spaceship.y + 1 }
            Direction.LEFT -> spaceships.any { it.x == spaceship.x - 1 && it.y == spaceship.y }
            Direction.RIGHT -> spaceships.any { it.x == spaceship.x + 1 && it.y == spaceship.y }
        }

        if (isBusy) {
            throw IllegalStateException("Failed to move spaceship. There is an other spaceship ahead.")
        }

        when (spaceship.direction) {
            Direction.UP -> spaceship.y -= 1
            Direction.RIGHT -> spaceship.x += 1
            Direction.DOWN -> spaceship.y += 1
            Direction.LEFT -> spaceship.x -= 1
        }
    }
}