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

    fun destroySpaceship(spaceship: Spaceship) {
        ensureStarted()
        ensureInGame(spaceship)

        spaceship.destroy()
        removeGameObject(spaceship)
    }

    fun getGameObjects(): List<GameObject> =
        gameObjects.toList()

    fun removeGameObject(gameObject: GameObject) {
        ensureStarted()
        ensureInGame(gameObject)

        gameObjects.remove(gameObject)
    }

    fun findCollidableObjectAt(position: Position): GameObject? {
        require(fieldSize.contains(position)) { "Position is out of the field" }
        return gameObjects.firstOrNull { it.type != Type.SPACESHIP && it.position == position }
    }

    fun isAsteroidAhead(spaceship: Spaceship): Boolean {
        ensureInGame(spaceship)

        return gameObjects.any { it.type == Type.ASTEROID && isPointAhead(spaceship, it) }
    }

    fun isGarbageAhead(spaceship: Spaceship): Boolean {
        ensureInGame(spaceship)

        return gameObjects.any { it.type == Type.GARBAGE && isPointAhead(spaceship, it) }
    }

    private fun isPointAhead(spaceship: Spaceship, gameObject: GameObject): Boolean {
        ensureInGame(spaceship)
        ensureInGame(gameObject)

        return gameObject.position == spaceship.position.aheadOf(spaceship.direction)
    }

    fun isWallAhead(spaceship: Spaceship): Boolean {
        ensureInGame(spaceship)

        return !fieldSize.contains(spaceship.position.aheadOf(spaceship.direction))
    }

    fun getSpaceships(): List<Spaceship> =
        spaceships.toList()

    fun damageSpaceship(spaceship: Spaceship, type: Type) {
        ensureStarted()
        ensureInGame(spaceship)

        when (type) {
            Type.ASTEROID -> spaceship.subtractScore()
            Type.GARBAGE -> {
                spaceship.addScore()
                onGarbageCollected()
            }
            Type.WALL -> rollbackSpaceship(spaceship)
            Type.SPACESHIP -> rollbackSpaceship(spaceship)
        }
    }

    private fun onGarbageCollected() {
        val isGarbageExists = gameObjects.any { it.type == Type.GARBAGE }
        if (!isGarbageExists) {
            finish()
        }
    }

    private fun rollbackSpaceship(spaceship: Spaceship) {
        spaceship.moveTo(spaceship.position.aheadOf(spaceship.direction.opposite()))
        spaceship.subtractScore()
    }

    fun generateAsteroids(count: Int) {
        ensureCreated()

        require(count > 0) { "Count must be greater than zero" }

        val asteroids = freeCoordinates()
            .take(count)
            .map(::Asteroid)
        gameObjects.addAll(asteroids)
    }

    fun generateGarbage(count: Int) {
        ensureCreated()

        require(count > 0) { "Count must be greater than zero" }

        val garbage = freeCoordinates()
            .take(count)
            .map(::Garbage)
        gameObjects.addAll(garbage)
    }

    fun createSpaceship(user: User): Spaceship {
        ensureCreated()

        val position = freeCoordinates()
            .first()
        val spaceship = Spaceship(position, user)
        gameObjects.add(spaceship)
        spaceships.add(spaceship)
        return spaceship
    }

    private fun freeCoordinates(): Sequence<Position> =
        sequence {
            while (true) {
                yield(generateUniqueRandomCoordinates())
            }
        }

    fun onSpaceshipMove(spaceship: Spaceship) {
        ensureStarted()
        ensureInGame(spaceship)

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

    fun isOutOfField(spaceship: Spaceship): Boolean =
        !fieldSize.contains(spaceship.position)

    private fun ensureCreated() {
        check(status == Status.CREATED) { "Game not in CREATED status" }
    }

    private fun ensureStarted() {
        check(status == Status.STARTED) { "Game not in STARTED status" }
    }

    private fun ensureInGame(spaceship: Spaceship) {
        require(spaceships.contains(spaceship)) { "Spaceship does not belong to the game" }
    }

    private fun ensureInGame(gameObject: GameObject) {
        require(gameObjects.contains(gameObject)) { "GameObject does not belong to the game" }
    }

    enum class Status {
        CREATED,
        STARTED,
        FINISHED,
    }
}
