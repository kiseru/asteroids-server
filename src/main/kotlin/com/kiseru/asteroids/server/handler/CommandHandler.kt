package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.GameField
import com.kiseru.asteroids.server.model.GameObject
import com.kiseru.asteroids.server.model.Player
import java.io.IOException

class CommandHandler(
    private val connectionHandler: ConnectionHandler,
    private val game: Game,
    private val player: Player,
    private val spaceship: GameObject.Spaceship,
) {

    fun handleCommand(command: Command?) =
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

    fun handleGo() {
        player.steps += 1
        if (player.steps > 1500) {
            handleDeath(player)
        } else {
            game.onSpaceshipMove(player, spaceship)
            if (player.status == Player.Status.Dead) {
                handleDeath(player)
            } else {
                connectionHandler.sendMessage(player.score.toString())
            }
        }
    }

    private fun handleDeath(player: Player) {
        player.status = Player.Status.Dead
        game.removeGameObject(spaceship)
        connectionHandler.sendMessage("died")
        connectionHandler.sendMessage("You've collected ${player.score} score")
    }

    fun handleLeft() {
        onChangeDirection(Direction.LEFT)
    }

    fun handleRight() {
        onChangeDirection(Direction.RIGHT)
    }

    fun handleUp() {
        onChangeDirection(Direction.UP)
    }

    fun handleDown() {
        onChangeDirection(Direction.DOWN)
    }

    private fun onChangeDirection(direction: Direction) {
        player.direction = direction
        connectionHandler.sendMessage("success")
    }

    fun handleIsAsteroid() {
        onBooleanSend(game.isAsteroidAhead(player.direction, spaceship))
    }

    fun handleIsGarbage() {
        onBooleanSend(game.isGarbageAhead(player.direction, spaceship))
    }

    fun handleIsWall() {
        onBooleanSend(game.isWallAhead(player.direction, spaceship))
    }

    private fun onBooleanSend(value: Boolean) {
        val message = if (value) "t" else "f"
        connectionHandler.sendMessage(message)
    }

    fun handleGameField() {
        try {
            val gameField = displayGameField(game.gameField)
            connectionHandler.sendMessage("$gameField\n")
        } catch (_: IOException) {
            println("Failed to write the room's game field")
        }
    }

    fun displayGameField(gameField: GameField): String {
        val stringBuilder = StringBuilder()
        for (i in 1 until gameField.height + 1) {
            for (j in 1 until gameField.width + 1) {
                val gameObject = gameField.objects.firstOrNull { it.x == j && it.y == i }
                val symbol = if (gameObject != null) view(gameObject) else "."
                val paddedSymbol = if (j == 1) symbol else symbol.padStart(3)
                stringBuilder.append(paddedSymbol)
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }

    private fun view(gameObject: GameObject): String =
        when (gameObject) {
            is GameObject.Asteroid -> "A"
            is GameObject.Garbage -> "G"
            is GameObject.Spaceship -> gameObject.id
        }

    fun handleUnknownCommand() {
        connectionHandler.sendMessage("Unknown command")
    }
}