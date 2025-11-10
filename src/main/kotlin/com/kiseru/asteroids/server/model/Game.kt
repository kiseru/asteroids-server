package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.model.GameObject.*
import java.util.Random

class Game(
    var gameField: GameField,
) {

    private val sendMessageHandlers = mutableListOf<(String) -> Unit>()
    private val players = mutableListOf<Pair<Player, Spaceship>>()

    private fun generateUniqueRandomCoordinates(): Pair<Int, Int> {
        if (gameField.objects.size == gameField.width * gameField.height) {
            throw IllegalStateException("No free space on the game field")
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
        gameField = gameField.copy(objects = gameField.objects + gameObject)
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

    fun freeCoordinates(): Sequence<Pair<Int, Int>> =
        sequence {
            while (true) {
                yield(generateUniqueRandomCoordinates())
            }
        }

    fun onSpaceshipMove(player: Player, spaceship: Spaceship) {
        val (x, y) = calculateNewPosition(spaceship, player.direction)

        if (isOutOfField(x, y)) {
            subtractScore(player)
        } else {
            val gameObject = gameField.objects.firstOrNull { it.x == x && it.y == y }
            when (gameObject) {
                is Asteroid -> subtractScore(player)
                is Garbage -> handleGarbageCollision(player, spaceship, gameObject)
                is Spaceship -> subtractScore(player)
                else -> moveSpaceship(player.direction, spaceship)
            }
        }
    }

    private fun calculateNewPosition(spaceship: Spaceship, direction: Direction): Pair<Int, Int> =
        when (direction) {
            Direction.UP -> spaceship.x to spaceship.y - 1
            Direction.DOWN -> spaceship.x to spaceship.y + 1
            Direction.LEFT -> spaceship.x - 1 to spaceship.y
            Direction.RIGHT -> spaceship.x + 1 to spaceship.y
        }

    private fun handleGarbageCollision(
        player: Player,
        spaceship: Spaceship,
        garbage: Garbage,
    ) {
        removeGameObject(garbage)
        moveSpaceship(player.direction, spaceship)
        addScore(player)
    }

    fun moveSpaceship(direction: Direction, spaceship: Spaceship) {
        when (direction) {
            Direction.UP -> spaceship.y -= 1
            Direction.RIGHT -> spaceship.x += 1
            Direction.DOWN -> spaceship.y += 1
            Direction.LEFT -> spaceship.x -= 1
        }
    }

    private fun addScore(player: Player) {
        player.score += 10
    }

    private fun subtractScore(player: Player) {
        player.score -= 50
        player.status = if (player.score >= 0) Player.Status.Alive else Player.Status.Dead
    }

    private fun isOutOfField(x: Int, y: Int): Boolean =
        x <= 0 || y <= 0 || x > gameField.width || y > gameField.height

    fun removeGameObject(gameObject: GameObject) {
        gameField = gameField.copy(objects = gameField.objects - gameObject)
    }

    fun hasGarbage(): Boolean =
        gameField.objects.any { it is Garbage }
}

