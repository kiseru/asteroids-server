package com.kiseru.asteroids.server.model

import java.util.Random
import java.util.UUID

class Game(
    val id: UUID,
    val name: String,
    val spaceshipCapacity: Int,
    var gameField: GameField,
) {

    var status = GameStatus.CREATED

    private val spaceships = mutableListOf<Spaceship>()
    private val sendMessageHandlers = mutableListOf<(String) -> Unit>()

    private fun generateUniqueRandomCoordinates(): Pair<Int, Int> {
        if (gameField.objects.size == gameField.width * gameField.height) {
            throw IllegalStateException()
        }

        return sequence {
            val random = Random()
            while (true) {
                yield(random.nextInt(gameField.width) + 1 to random.nextInt(gameField.height) + 1)
            }
        }
            .filterNot { (x, y) -> isGameObjectsContainsCoordinates(x, y) }
            .first()
    }

    private fun isGameObjectsContainsCoordinates(x: Int, y: Int): Boolean =
        gameField.objects.any { it.x == x && it.y == y }

    fun addGameObject(gameObject: GameObject) {
        println("addGameObject")
        val gameObjects = gameField.objects + gameObject
        gameField = gameField.copy(objects = gameObjects)
    }

    fun addGameObjects(gameObjects: List<GameObject>) {
        println("addGameObjects")
        val gameObjects = gameField.objects + gameObjects
        gameField = gameField.copy(objects = gameObjects)
    }

    fun isAsteroidAhead(spaceship: Spaceship): Boolean =
        gameField.objects.any { it.type == Type.ASTEROID && isPointAhead(spaceship, it) }

    fun isGarbageAhead(spaceship: Spaceship): Boolean =
        gameField.objects.any { it.type == Type.GARBAGE && isPointAhead(spaceship, it) }

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
            Direction.DOWN -> spaceship.y == gameField.height
            Direction.LEFT -> spaceship.x == 1
            Direction.RIGHT -> spaceship.x == gameField.width
        }

    fun addSpaceship(spaceship: Spaceship, onMessageSend: (String) -> Unit) {
        spaceships.add(spaceship)
        sendMessageHandlers.add(onMessageSend)
    }

    fun getSpaceships(): List<Spaceship> =
        spaceships

    fun getSendMessageHandlers(): List<(String) -> Unit> =
        sendMessageHandlers

    fun onGarbageCollected() {
        val isGarbageExists = gameField.objects.any { it.type == Type.GARBAGE }
        if (!isGarbageExists) {
            status = GameStatus.FINISHED
        }
    }

    fun rollback(spaceship: Spaceship) {
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
        if (status != GameStatus.STARTED) {
            throw IllegalStateException("Failed to move the spaceship because of the game's illegal status")
        }

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

    fun removeGameObject(gameObject: GameObject) {
        val gameObjects = gameField.objects.filter { it != gameObject }
        gameField = gameField.copy(objects = gameObjects)
    }
}

