package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.SpaceshipHandler
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Spaceship

class SpaceshipHandlerImpl(
    private val spaceship: Spaceship,
    private val game: Game,
    private val onMessageSend: (String) -> Unit,
) : SpaceshipHandler {

    override fun onIncrementSteps() {
        spaceship.steps += 1
        if (spaceship.steps >= 1500 || spaceship.score.value < 0) {
            handleDeath()
        }
    }

    private fun handleDeath() {
        onMessageSend("died")
        onMessageSend("You've collected ${spaceship.score.value} score")
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
        onMessageSend(spaceship.score.value.toString())
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
        game.onSpaceshipMove(spaceship)
    }

    override fun onSpaceshipChangeDirection(direction: Direction) {
        spaceship.changeDirection(direction)
        onSuccess()
    }
}