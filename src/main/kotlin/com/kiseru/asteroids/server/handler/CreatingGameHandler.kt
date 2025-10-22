package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.GameField
import com.kiseru.asteroids.server.model.GameObject.Asteroid
import com.kiseru.asteroids.server.model.GameObject.Garbage
import com.kiseru.asteroids.server.model.GameObject.Spaceship
import com.kiseru.asteroids.server.model.User
import java.net.Socket
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

private const val GARBAGE_AMOUNT = 10
private const val ASTEROIDS_AMOUNT = 10
private const val GAME_FIELD_HEIGHT = 10
private const val GAME_FIELD_WIDTH = 10
private const val SPACESHIP_PER_GAME = 2

class CreatingGameHandler(private val connectionQueue: BlockingQueue<Socket>) {

    fun handle() {
        generateSequence { connectionQueue.take() }
            .chunked(SPACESHIP_PER_GAME)
            .forEach { thread { handleConnection(it) } }
    }

    private fun handleConnection(sockets: List<Socket>) {
        val connectionHandlers = sockets.map { ConnectionHandler(it) }
        val users = createUsers(connectionHandlers)
        startGame(users)
    }

    private fun createUsers(connectionHandlers: List<ConnectionHandler>): List<Pair<User, ConnectionHandler>> {
        val users = CopyOnWriteArrayList<Pair<User, ConnectionHandler>>()
        val countDownLatch = CountDownLatch(connectionHandlers.size)
        connectionHandlers.forEach {
            thread {
                try {
                    val user = createUser(it)
                    users.add(user)
                    it.sendMessage("Waiting another users")
                } finally {
                    countDownLatch.countDown()
                }
            }
        }
        countDownLatch.await()
        return users
    }

    private fun createUser(connectionHandler: ConnectionHandler): Pair<User, ConnectionHandler> {
        connectionHandler.sendMessage("Welcome To Asteroids Server")
        connectionHandler.sendMessage("Please, introduce yourself!")
        val username = connectionHandler.receiveMessage()
        val user = User(username)
        return user to connectionHandler
    }

    private fun startGame(users: List<Pair<User, ConnectionHandler>>) {
        val (game, spaceships) = createGame()
        users.forEach { (user, connectionHandler) ->
            val spaceship = spaceships.poll()
            val gameHandler = GameHandler(connectionHandler, user, game, spaceship)
            thread { gameHandler.handle() }
        }
    }

    private fun createGame(): Pair<Game, Queue<Spaceship>> {
        val gameField = GameField(GAME_FIELD_WIDTH, GAME_FIELD_HEIGHT)
        val game = Game(gameField)
        val spaceships = generateGameObjects(game)
        return game to spaceships
    }

    private fun generateGameObjects(game: Game): Queue<Spaceship> {
        val freeCoordinates = game.freeCoordinates()
            .distinct()
            .take(ASTEROIDS_AMOUNT + GARBAGE_AMOUNT + SPACESHIP_PER_GAME)
            .toList()
        val garbage = freeCoordinates.subList(0, GARBAGE_AMOUNT)
            .map { (x, y) -> Garbage(x, y) }
        val asteroids = freeCoordinates.subList(GARBAGE_AMOUNT, GARBAGE_AMOUNT + ASTEROIDS_AMOUNT)
            .map { (x, y) -> Asteroid(x, y) }
        game.addGameObjects(garbage + asteroids)
        return freeCoordinates.subList(GARBAGE_AMOUNT + ASTEROIDS_AMOUNT, freeCoordinates.size)
            .mapIndexed { index, (x, y) -> Spaceship(x, y, (index + 1).toString()) }
            .toCollection(LinkedList())
    }
}
