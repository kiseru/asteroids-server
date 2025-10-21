package com.kiseru.asteroids.server.impl

import com.kiseru.asteroids.server.ConnectionReceiver
import com.kiseru.asteroids.server.handler.impl.GameHandlerImpl
import com.kiseru.asteroids.server.handler.impl.SpaceshipHandlerImpl
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Asteroid
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.GameField
import com.kiseru.asteroids.server.model.GameStatus
import com.kiseru.asteroids.server.model.Garbage
import com.kiseru.asteroids.server.model.Player
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.GameService
import java.io.IOException
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.Random
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

private const val GARBAGE_AMOUNT = 150
private const val ASTEROIDS_AMOUNT = 150
private const val GAME_FIELD_HEIGHT = 30
private const val GAME_FIELD_WIDTH = 30
private const val SPACESHIP_PER_GAME = 1

class ConnectionReceiverImpl(
    private val serverSocket: ServerSocket,
    private val gameService: GameService,
) : ConnectionReceiver {

    override fun acceptConnections(): Unit =
        connections()
            .forEach(::handleConnection)

    private fun connections(): Sequence<Socket> =
        sequence {
            while (true) {
                val socket = serverSocket.accept()
                yield(socket)
            }
        }

    private fun handleConnection(socket: Socket) {
        try {
            val inputStream = socket.getInputStream()
            val outputStream = socket.getOutputStream()
            val printWriter = PrintWriter(outputStream, true)
            val reader = inputStream.bufferedReader()
            handleConnection(reader::readLine, printWriter::println)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun handleConnection(onMessageReceive: () -> String, onMessageSend: (String) -> Unit) {
        try {
            val username = promptUsername(onMessageReceive, onMessageSend)
            val user = User(Random().nextInt(100), username)
            thread { handleUser(user, onMessageReceive, onMessageSend) }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun handleUser(user: User, onMessageReceive: () -> String, onMessageSend: (String) -> Unit) {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        println("${user.username} has joined the server!")
        val game = createGame()
        val gameHandler = GameHandlerImpl(game, lock, condition, gameService)
        gameService.addGame(game)
        val player = Player()
        val spaceship = createSpaceship(game, user)
        game.addGameObject(spaceship)
        game.addSpaceship(player, spaceship, onMessageSend)
        val spaceshipHandler = SpaceshipHandlerImpl(game, onMessageSend)
        spaceshipHandler.onRoomJoin(game.name)
        try {
            spaceshipHandler.onSendInstructions(spaceship)
            thread { gameHandler.handle() }
            lock.withLock {
                while (game.status != GameStatus.STARTED) {
                    condition.await()
                }
            }

            while (game.status != GameStatus.FINISHED && player.status == Player.Status.Alive) {
                val userMessage = onMessageReceive()
                when (userMessage) {
                    "go" -> {
                        spaceshipHandler.onSpaceshipMove(player.direction, spaceship)
                        gameHandler.checkSpaceship(player, spaceship)
                        spaceshipHandler.onSendScore(player)
                    }
                    "left" -> spaceshipHandler.onSpaceshipChangeDirection(player, Direction.LEFT)
                    "right" -> spaceshipHandler.onSpaceshipChangeDirection(player, Direction.RIGHT)
                    "up" -> spaceshipHandler.onSpaceshipChangeDirection(player, Direction.UP)
                    "down" -> spaceshipHandler.onSpaceshipChangeDirection(player, Direction.DOWN)
                    "isAsteroid" -> spaceshipHandler.onIsAsteroid(player, spaceship)
                    "isGarbage" -> spaceshipHandler.onIsGarbage(player, spaceship)
                    "isWall" -> spaceshipHandler.onIsWall(player, spaceship)
                    "GAME_FIELD" -> gameService.writeGameField(game, onMessageSend)
                    else -> spaceshipHandler.onUnknownCommand()
                }
                spaceshipHandler.onIncrementSteps(player)
            }
        } catch (_: IOException) {
            println("Connection problems with user " + user.username)
        } finally {
            player.status = Player.Status.Dead
            lock.withLock {
                val aliveUsersCount = game.getPlayers().count { (player, _) -> player.status == Player.Status.Alive }
                if (aliveUsersCount == 0) {
                    game.status = GameStatus.FINISHED
                }
                condition.signalAll()
            }
        }
    }

    private fun promptUsername(onReceiveMessage: () -> String, onMessageSend: (String) -> Unit): String {
        onMessageSend("Welcome To Asteroids Server")
        onMessageSend("Please, introduce yourself!")
        return onReceiveMessage()
    }

    private fun createGame(): Game {
        val gameId = UUID.randomUUID()
        val gameField = GameField(GAME_FIELD_WIDTH, GAME_FIELD_HEIGHT)
        val game = Game(gameId, gameId.toString(), SPACESHIP_PER_GAME, gameField)
        generateGameObjects(game)
        return game
    }

    private fun generateGameObjects(game: Game) {
        val freeCoordinates = game.freeCoordinates()
            .distinct()
            .take(ASTEROIDS_AMOUNT + GARBAGE_AMOUNT)
            .toList()
        val garbage = freeCoordinates.subList(0, GARBAGE_AMOUNT)
            .map { (x, y) -> Garbage(x, y) }
        val asteroids = freeCoordinates.subList(GARBAGE_AMOUNT, GARBAGE_AMOUNT + ASTEROIDS_AMOUNT)
            .map { (x, y) -> Asteroid(x, y) }
        game.addGameObjects(garbage + asteroids)
    }

    private fun createSpaceship(game: Game, user: User): Spaceship {
        val (x, y) = game.freeCoordinates()
            .first()
        return Spaceship(x, y, user)
    }
}
