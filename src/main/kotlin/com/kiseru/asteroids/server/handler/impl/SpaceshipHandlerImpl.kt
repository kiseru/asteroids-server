package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.SpaceshipHandler
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates
import com.kiseru.asteroids.server.logics.auxiliary.Direction
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Spaceship

class SpaceshipHandlerImpl(
    private val spaceship: Spaceship,
    private val game: Game,
    private val onMessageSend: (String) -> Unit,
) : SpaceshipHandler {

    override fun onIncrementSteps() {
        spaceship.steps += 1
        if (spaceship.steps >= 1500 || spaceship.score < 0) {
            handleDeath()
        }
    }

    private fun handleDeath() {
        spaceship.isAlive = false
        onMessageSend("died")
        onMessageSend("You've collected ${spaceship.score} score")
    }

    override fun onSendInstructions() {
        onMessageSend("You need to keep a space garbage.")
        onMessageSend("Your ID is ${spaceship.user.id}")
        onMessageSend("Good luck, Commander!")
    }

    override fun onRoomJoin(roomName: String) {
        onMessageSend("You joined the room \"$roomName\"")
    }

    override fun onSendScore() {
        onMessageSend(spaceship.score.toString())
    }

    override fun onSuccess() {
        onMessageSend("success")
    }

    override fun onIsAsteroid() {
        onBooleanSend(game.isAsteroidAhead(spaceship))
    }

    override fun onIsGarbage() {
        onBooleanSend(game.isGarbageAhead(spaceship))
    }

    override fun onIsWall() {
        onBooleanSend(game.isWallAhead(spaceship))
    }

    private fun onBooleanSend(value: Boolean) {
        val message = if (value) "t" else "f"
        onMessageSend(message)
    }

    override fun onUnknownCommand() {
        onMessageSend("Unknown command")
    }

    override fun onSpaceshipMove() {
        spaceship.coordinates = when (spaceship.direction) {
            Direction.UP -> Coordinates(spaceship.x, spaceship.y - 1)
            Direction.RIGHT -> Coordinates(spaceship.x + 1, spaceship.y)
            Direction.DOWN -> Coordinates(spaceship.x, spaceship.y + 1)
            Direction.LEFT -> Coordinates(spaceship.x - 1, spaceship.y)
        }
    }

    override fun onSpaceshipChangeDirection(direction: Direction) {
        spaceship.direction = direction
        onSuccess()
    }
}