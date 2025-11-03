package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.GameObject
import com.kiseru.asteroids.server.model.GameObject.Asteroid
import com.kiseru.asteroids.server.model.GameObject.Garbage
import com.kiseru.asteroids.server.model.GameObject.Spaceship
import com.kiseru.asteroids.server.model.Player

class CommandHandler(
    private val game: Game,
    private val player: Player,
    private val spaceship: Spaceship,
) {

    fun handleCommand(command: Command?): String =
        when (command) {
            Command.Go -> handleGo()
            Command.Left -> handleLeft()
            Command.Right -> handleRight()
            Command.Up -> handleUp()
            Command.Down -> handleDown()
            Command.IsAsteroid -> handleIsAsteroid()
            Command.IsGarbage -> handleIsGarbage()
            Command.IsWall -> handleIsWall()
            Command.GameField -> handleGameField()
            else -> handleUnknownCommand()
        }

    private fun handleGo(): String =
        if (player.steps >= 1500) {
            handleDeath(player)
        } else {
            game.onSpaceshipMove(player, spaceship)
            if (player.status == Player.Status.Dead) {
                handleDeath(player)
            } else {
                player.steps += 1
                player.score.toString()
            }
        }

    private fun handleDeath(player: Player): String {
        player.status = Player.Status.Dead
        game.removeGameObject(spaceship)
        return "died\nYou've collected ${player.score} score"
    }

    private fun handleLeft(): String =
        onChangeDirection(Direction.LEFT)

    private fun handleRight(): String =
        onChangeDirection(Direction.RIGHT)

    private fun handleUp(): String =
        onChangeDirection(Direction.UP)

    private fun handleDown(): String =
        onChangeDirection(Direction.DOWN)

    private fun onChangeDirection(direction: Direction): String {
        player.direction = direction
        return "success"
    }

    private fun handleIsAsteroid(): String =
        onBooleanSend(game.isAsteroidAhead(player.direction, spaceship))

    private fun handleIsGarbage(): String =
        onBooleanSend(game.isGarbageAhead(player.direction, spaceship))

    private fun handleIsWall(): String =
        onBooleanSend(game.isWallAhead(player.direction, spaceship))

    private fun onBooleanSend(value: Boolean): String =
        if (value) "t" else "f"

    private fun handleGameField(): String {
        val stringBuilder = StringBuilder()
        for (i in 1 until game.gameField.height + 1) {
            for (j in 1 until game.gameField.width + 1) {
                val gameObject = game.gameField.objects.firstOrNull { it.x == j && it.y == i }
                val symbol = if (gameObject != null) view(gameObject) else "."
                val paddedSymbol = if (j == 1) symbol else symbol.padStart(3)
                stringBuilder.append(paddedSymbol)
            }
            stringBuilder.append("\n")
        }
        return stringBuilder
            .append("\n")
            .toString()
    }

    private fun view(gameObject: GameObject): String =
        when (gameObject) {
            is Asteroid -> "A"
            is Garbage -> "G"
            is Spaceship -> gameObject.id
        }

    private fun handleUnknownCommand(): String =
        "Unknown command"
}
