package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.handler.SpaceshipHandler
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Player
import com.kiseru.asteroids.server.model.Spaceship

class SpaceshipHandlerImpl(
    private val game: Game,
    private val onMessageSend: (String) -> Unit,
) : SpaceshipHandler {

    override fun onIncrementSteps(player: Player) {
        player.steps += 1
        if (player.steps >= 1500 || player.score < 0) {
            handleDeath(player)
        }
    }

    private fun handleDeath(player: Player) {
        player.isAlive = false
        onMessageSend("died")
        onMessageSend("You've collected ${player.score} score")
    }

    override fun onSendInstructions(spaceship: Spaceship) {
        onMessageSend("You need to keep a space garbage.")
        onMessageSend("Your ID is ${spaceship.user.id}")
        onMessageSend("Good luck, Commander!")
    }

    override fun onRoomJoin(roomName: String) {
        onMessageSend("You joined the room \"$roomName\"")
    }

    override fun onSendScore(player: Player) {
        onMessageSend(player.score.toString())
    }

    override fun onSuccess() {
        onMessageSend("success")
    }

    override fun onIsAsteroid(player: Player, spaceship: Spaceship) {
        onBooleanSend(game.isAsteroidAhead(player.direction, spaceship))
    }

    override fun onIsGarbage(player: Player, spaceship: Spaceship) {
        onBooleanSend(game.isGarbageAhead(player.direction, spaceship))
    }

    override fun onIsWall(player: Player, spaceship: Spaceship) {
        onBooleanSend(game.isWallAhead(player.direction, spaceship))
    }

    private fun onBooleanSend(value: Boolean) {
        val message = if (value) "t" else "f"
        onMessageSend(message)
    }

    override fun onUnknownCommand() {
        onMessageSend("Unknown command")
    }

    override fun onSpaceshipMove(direction: Direction, spaceship: Spaceship) {
        game.onSpaceshipMove(direction, spaceship)
    }

    override fun onSpaceshipChangeDirection(player: Player, direction: Direction) {
        player.direction = direction
        onSuccess()
    }
}