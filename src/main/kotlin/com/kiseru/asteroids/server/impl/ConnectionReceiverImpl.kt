package com.kiseru.asteroids.server.impl

import com.kiseru.asteroids.server.ConnectionReceiver
import com.kiseru.asteroids.server.handler.impl.GameHandlerImpl
import com.kiseru.asteroids.server.handler.impl.SpaceshipHandlerImpl
import com.kiseru.asteroids.server.messaging.GameMessageBroadcaster
import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.FieldSize
import com.kiseru.asteroids.server.model.Game
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

        val gameMessageBroadcaster = GameMessageBroadcaster()
        gameMessageBroadcaster.addRecipient(onMessageSend)

        val gameHandler = GameHandlerImpl(game, gameMessageBroadcaster, lock, condition, gameService)
        val spaceship = game.createSpaceship(user)
        val spaceshipHandler = SpaceshipHandlerImpl(spaceship, game, onMessageSend)
        spaceshipHandler.onRoomJoin(game.name)
        try {
            spaceshipHandler.onSendInstructions()
            thread { gameHandler.handle() }
            lock.withLock {
                while (game.status != Game.Status.STARTED) {
                    condition.await()
                }
            }

            while (game.status != Game.Status.FINISHED && spaceship.status == Spaceship.Status.ALIVE) {
                val userMessage = onMessageReceive()
                when (userMessage) {
                    "go" -> {
                        spaceshipHandler.onSpaceshipMove()
                        gameHandler.checkSpaceship(spaceship)
                        spaceshipHandler.onSendScore()
                    }

                    "left" -> spaceshipHandler.onSpaceshipChangeDirection(Direction.LEFT)

                    "right" -> spaceshipHandler.onSpaceshipChangeDirection(Direction.RIGHT)

                    "up" -> spaceshipHandler.onSpaceshipChangeDirection(Direction.UP)

                    "down" -> spaceshipHandler.onSpaceshipChangeDirection(Direction.DOWN)

                    "isAsteroid" -> spaceshipHandler.onIsAsteroid()

                    "isGarbage" -> spaceshipHandler.onIsGarbage()

                    "isWall" -> spaceshipHandler.onIsWall()

                    "GAME_FIELD" -> gameService.writeGameField(game, onMessageSend)

                    else -> spaceshipHandler.onUnknownCommand()
                }
                spaceshipHandler.onIncrementSteps()
            }
        } catch (e: IOException) {
            println("Connection problems with user " + user.username)
        } finally {
            if (spaceship.status == Spaceship.Status.ALIVE) {
                spaceship.destroy()
            }
            lock.withLock {
                val aliveUsersCount = game.getSpaceships().count { it.status == Spaceship.Status.ALIVE }
                if (aliveUsersCount == 0) {
                    game.finish()
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
        val fieldSize = FieldSize(GAME_FIELD_WIDTH, GAME_FIELD_HEIGHT)
        val game = Game(gameId, gameId.toString(), 1, fieldSize)
        game.generateGarbage(GARBAGE_AMOUNT)
        game.generateAsteroids(ASTEROIDS_AMOUNT)
        return game
    }
}
