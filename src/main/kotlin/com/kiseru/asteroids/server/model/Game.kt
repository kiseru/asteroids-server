package com.kiseru.asteroids.server.model

import java.util.Random
import java.util.UUID

class Game(
    val id: UUID,
    val name: String,
    val size: Int,
    val fieldSize: FieldSize,
) {

    private val gameObjects = mutableListOf<GameObject>()

    var status = Status.CREATED
        private set

    private val spaceships = mutableListOf<Spaceship>()

    private fun generateUniqueRandomCoordinates(): Position {
        if (gameObjects.size.toLong() >= fieldSize.capacity) {
            throw IllegalStateException("No free coordinates")
        }

        return sequence {
            val random = Random()
            while (true) {
                yield(Position(random.nextInt(fieldSize.width) + 1, random.nextInt(fieldSize.height) + 1))
            }
        }
            .filterNot { position -> isGameObjectsContainsCoordinates(position) }
            .first()
    }

    private fun isGameObjectsContainsCoordinates(position: Position): Boolean =
        gameObjects.any { it.position == position }

    fun getGameObjects(): List<GameObject> =
        gameObjects.toList()

    fun addGameObject(gameObject: GameObject) {
        ensureCreated()

        gameObjects.add(gameObject)
    }

    fun removeGameObject(gameObject: GameObject) {
        ensureStarted()

        gameObjects.remove(gameObject)
    }

    fun findCollidableObjectAt(position: Position): GameObject? {
        require(fieldSize.contains(position)) { "Position is out of the field" }
        return gameObjects.firstOrNull { it.type != Type.SPACESHIP && it.position == position }
    }

    fun isAsteroidAhead(spaceship: Spaceship): Boolean =
        gameObjects.any { it.type == Type.ASTEROID && isPointAhead(spaceship, it) }

    fun isGarbageAhead(spaceship: Spaceship): Boolean =
        gameObjects.any { it.type == Type.GARBAGE && isPointAhead(spaceship, it) }

    private fun isPointAhead(spaceship: Spaceship, gameObject: GameObject): Boolean =
        gameObject.position == spaceship.position.aheadOf(spaceship.direction)

    fun isWallAhead(spaceship: Spaceship): Boolean =
        !fieldSize.contains(spaceship.position.aheadOf(spaceship.direction))

    fun addSpaceship(spaceship: Spaceship) {
        spaceships.add(spaceship)
    }

    fun getSpaceships(): List<Spaceship> =
        spaceships.toList()

    fun onGarbageCollected() {
        val isGarbageExists = gameObjects.any { it.type == Type.GARBAGE }
        if (!isGarbageExists) {
            finish()
        }
    }

    fun rollbackSpaceship(spaceship: Spaceship) {
        ensureStarted()

        spaceship.moveTo(spaceship.position.aheadOf(spaceship.direction.opposite()))
        spaceship.subtractScore()
    }

    fun freeCoordinates(): Sequence<Position> =
        sequence {
            while (true) {
                yield(generateUniqueRandomCoordinates())
            }
        }

    fun onSpaceshipMove(spaceship: Spaceship) {
        if (status != Status.STARTED) {
            throw IllegalStateException("Failed to move the spaceship because of the game's illegal status")
        }

        val aheadOfSpaceship = spaceship.position.aheadOf(spaceship.direction)
        val isBusy = spaceships.any { it.position == aheadOfSpaceship }

        if (isBusy) {
            throw IllegalStateException("Failed to move spaceship. There is an other spaceship ahead.")
        }

        spaceship.moveTo(aheadOfSpaceship)
    }

    fun start() {
        ensureCreated()

        status = Status.STARTED
    }

    fun finish() {
        ensureStarted()

        status = Status.FINISHED
    }

    private fun ensureCreated() {
        check(status == Status.CREATED) { "Game not in CREATED status" }
    }

    private fun ensureStarted() {
        check(status == Status.STARTED) { "Game not in STARTED status" }
    }

    enum class Status {
        CREATED,
        STARTED,
        FINISHED,
    }
}
