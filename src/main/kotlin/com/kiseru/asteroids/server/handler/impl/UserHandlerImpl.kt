package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.handler.UserHandler
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates
import com.kiseru.asteroids.server.logics.auxiliary.Direction

class UserHandlerImpl(
    private val user: User,
    private val onMessageSend: (String) -> Unit,
) : UserHandler {

    override fun onIncrementSteps() {
        user.spaceship.steps += 1
        if (user.spaceship.steps >= 1500 || user.spaceship.score < 0) {
            handleDeath()
        }
    }

    private fun handleDeath() {
        user.spaceship.isAlive = false
        onMessageSend("died")
        val scoreMessage = String.format("You have collected %d score", user.spaceship.score)
        onMessageSend(scoreMessage)
    }

    override fun onSendInstructions() {
        onMessageSend("You need to keep a space garbage.")
        onMessageSend("Your ID is " + user.id)
        onMessageSend("Good luck, Commander!")
    }

    override fun onRoomJoin(roomName: String) {
        onMessageSend("You joined the room \"$roomName\"")
    }

    override fun onSendScore() {
        onMessageSend(user.spaceship.score.toString())
    }

    override fun onSuccess() {
        onMessageSend("success")
    }

    override fun onIsAsteroid() {
        onBooleanSend(user.spaceship.courseChecker.isAsteroid(user.spaceship))
    }

    override fun onIsGarbage() {
        onBooleanSend(user.spaceship.courseChecker.isGarbage(user.spaceship))
    }

    override fun onIsWall() {
        onBooleanSend(user.spaceship.courseChecker.isWall(user.spaceship))
    }

    private fun onBooleanSend(value: Boolean) {
        val message = if (value) "t" else "f"
        onMessageSend(message)
    }

    override fun onUnknownCommand() {
        onMessageSend("Unknown command")
    }

    override fun onSpaceshipMove() {
        val direction = user.spaceship.direction ?: return
        user.spaceship.coordinates = when (direction) {
            Direction.UP -> Coordinates(user.spaceship.x, user.spaceship.y - 1)
            Direction.RIGHT -> Coordinates(user.spaceship.x + 1, user.spaceship.y)
            Direction.DOWN -> Coordinates(user.spaceship.x, user.spaceship.y + 1)
            Direction.LEFT -> Coordinates(user.spaceship.x - 1, user.spaceship.y)
        }
    }

    override fun onSpaceshipChangeDirection(direction: Direction) {
        user.spaceship.direction = direction
    }
}
