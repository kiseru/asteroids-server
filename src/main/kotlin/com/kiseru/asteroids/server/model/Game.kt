package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.model.GameObject.*
import java.util.Random
import java.util.UUID

class Game(
    val id: UUID,
    val name: String,
    val spaceshipCapacity: Int,
    var gameField: GameField,
) {

    var status = GameStatus.CREATED

    private val sendMessageHandlers = mutableListOf<(String) -> Unit>()
    private val players = mutableListOf<Pair<Player, Spaceship>>()

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
        val gameObjects = gameField.objects + gameObject
        gameField = gameField.copy(objects = gameObjects)
    }

    fun addGameObjects(gameObjects: List<GameObject>) {
        val gameObjects = gameField.objects + gameObjects
        gameField = gameField.copy(objects = gameObjects)
    }

    fun isAsteroidAhead(direction: Direction, spaceship: Spaceship): Boolean =
        gameField.objects.any { it is Asteroid && isPointAhead(direction, spaceship, it) }

    fun isGarbageAhead(direction: Direction, spaceship: Spaceship): Boolean =
        gameField.objects.any { it is Garbage && isPointAhead(direction, spaceship, it) }

    private fun isPointAhead(direction: Direction, spaceship: Spaceship, gameObject: GameObject): Boolean =
        when (direction) {
            Direction.UP -> spaceship.x == gameObject.x && spaceship.y == gameObject.y + 1
            Direction.DOWN -> spaceship.x == gameObject.x && spaceship.y == gameObject.y - 1
            Direction.LEFT -> spaceship.x == gameObject.x + 1 && spaceship.y == gameObject.y
            Direction.RIGHT -> spaceship.x == gameObject.x - 1 && spaceship.y == gameObject.y
        }

    fun isWallAhead(direction: Direction, spaceship: Spaceship): Boolean =
        when (direction) {
            Direction.UP -> spaceship.y == 1
            Direction.DOWN -> spaceship.y == gameField.height
            Direction.LEFT -> spaceship.x == 1
            Direction.RIGHT -> spaceship.x == gameField.width
        }

    fun addSpaceship(player: Player, spaceship: Spaceship, onMessageSend: (String) -> Unit) {
        players.add(player to spaceship)
        sendMessageHandlers.add(onMessageSend)
    }

    fun getPlayers(): List<Pair<Player, Spaceship>> =
        players

    fun getSendMessageHandlers(): List<(String) -> Unit> =
        sendMessageHandlers

    fun onGarbageCollected() {
        val isGarbageExists = gameField.objects.any { it is Garbage }
        if (!isGarbageExists) {
            status = GameStatus.FINISHED
        }
    }

    fun rollback(direction: Direction, spaceship: Spaceship) {
        when (direction) {
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

    fun onSpaceshipMove(direction: Direction, spaceship: Spaceship) {
        if (status != GameStatus.STARTED) {
            throw IllegalStateException("Failed to move the spaceship because of the game's illegal status")
        }

        val isBusy = when (direction) {
            Direction.UP -> players.any {  it.second.x == spaceship.x && it.second.y == spaceship.y - 1 }
            Direction.DOWN -> players.any { it.second.x == spaceship.x && it.second.y == spaceship.y + 1 }
            Direction.LEFT -> players.any { it.second.x == spaceship.x - 1 && it.second.y == spaceship.y }
            Direction.RIGHT -> players.any { it.second.x == spaceship.x + 1 && it.second.y == spaceship.y }
        }

        if (isBusy) {
            throw IllegalStateException("Failed to move spaceship. There is an other spaceship ahead.")
        }

        when (direction) {
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

