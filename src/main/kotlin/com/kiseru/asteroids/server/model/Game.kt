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

    private fun generateUniqueRandomCoordinates(): Position {
        if (gameObjects.size == fieldWidth * fieldHeight) {
            throw IllegalStateException()
        }

        return sequence {
            val random = Random()
            while (true) {
                yield(Position(random.nextInt(fieldWidth) + 1, random.nextInt(fieldHeight) + 1))
            }
        }
            .filterNot { position -> isGameObjectsContainsCoordinates(position) }
            .first()
    }

    private fun isGameObjectsContainsCoordinates(position: Position): Boolean =
        gameObjects.any { it.position == position }

    fun addGameObject(gameObject: GameObject) {
        gameObjects.add(gameObject)
    }

    fun isAsteroidAhead(spaceship: Spaceship): Boolean =
        gameObjects.any { it.type == Type.ASTEROID && isPointAhead(spaceship, it) }

    fun isGarbageAhead(spaceship: Spaceship): Boolean =
        gameObjects.any { it.type == Type.GARBAGE && isPointAhead(spaceship, it) }

    private fun isPointAhead(spaceship: Spaceship, gameObject: GameObject): Boolean =
        gameObject.position == spaceship.position.aheadOf(spaceship.direction)

    fun isWallAhead(spaceship: Spaceship): Boolean =
        spaceship.position.aheadOf(spaceship.direction).isOutOfBounds(fieldWidth, fieldHeight)

    fun addSpaceship(spaceship: Spaceship, onMessageSend: (String) -> Unit) {
        spaceships.add(spaceship)
        sendMessageHandlers.add(onMessageSend)
    }

    fun getSpaceships(): List<Spaceship> =
        spaceships

    fun getSendMessageHandlers(): List<(String) -> Unit> =
        sendMessageHandlers

    fun onGarbageCollected() {
        val isGarbageExists = gameObjects.any { it.type == Type.GARBAGE }
        if (!isGarbageExists) {
            status = GameStatus.FINISHED
        }
    }

    fun rollback(spaceship: Spaceship) {
        spaceship.position = spaceship.position.aheadOf(spaceship.direction.opposite())
    }

    fun freeCoordinates(): Sequence<Position> =
        sequence {
            while (true) {
                yield(generateUniqueRandomCoordinates())
            }
        }

    fun onSpaceshipMove(spaceship: Spaceship) {
        if (status != GameStatus.STARTED) {
            throw IllegalStateException("Failed to move the spaceship because of the game's illegal status")
        }

        val aheadOfSpaceship = spaceship.position.aheadOf(spaceship.direction)
        val isBusy = spaceships.any { it.position == aheadOfSpaceship }

        if (isBusy) {
            throw IllegalStateException("Failed to move spaceship. There is an other spaceship ahead.")
        }

        spaceship.position = aheadOfSpaceship
    }
}
