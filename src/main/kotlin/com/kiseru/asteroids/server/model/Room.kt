package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.logics.Game
import com.kiseru.asteroids.server.service.RoomService
import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Комната.
 */
class Room : Runnable {

    val users: MutableList<User> = CopyOnWriteArrayList()

    val isFull: Boolean
        get() = users.size >= MAX_USERS

    val isGameStarted: Boolean
        get() = status == Status.GAMING

    val isGameFinished: Boolean
        get() = status == Status.FINISHED

    lateinit var game: Game
        private set

    private val lock: Lock = ReentrantLock()

    private val endgameCondition = lock.newCondition()

    private val spaceShipCreatedCondition = lock.newCondition()

    private var status = Status.WAITING_CONNECTIONS

    override fun run() {
        RoomService.sendMessageToUsers(this, "start")
        status = Status.GAMING
        lock.withLock {
            game = Game(Screen(SCREEN_WIDTH, SCREEN_HEIGHT), NUMBER_OF_GARBAGE_CELLS, NUMBER_OF_ASTEROID_CELLS)
            for (user in users) {
                game.registerSpaceShipForUser(user)
            }
            spaceShipCreatedCondition.signalAll()
        }

        game.refresh()

        lock.withLock {
            while (status != Status.FINISHED) {
                endgameCondition.await()
            }
        }

        val rating = RoomService.getRoomRating(this)
        RoomService.sendMessageToUsers(this, "finish\n$rating")
        log.info("Room released! Rating table:\n$rating")
    }

    /**
     * Проверяет, собрали ли весь мусор
     *
     * @param collected количество собранного мусора
     */
    fun checkCollectedGarbage(collected: Int) {
        if (collected >= game.garbageNumber) {
            setGameFinished()
        }
    }

    /**
     * Переводит игру в статус завершен.
     */
    fun setGameFinished() = lock.withLock {
        if (status == Status.FINISHED) {
            return
        }

        status = Status.FINISHED
        log.info("Game finished")
        endgameCondition.signalAll()
    }

    fun addUserToRoom(user: User) = lock.withLock {
        addUser(user)
        if (isFull) {
            RoomService.getNotFullRoom()
            EXECUTOR_SERVICE.execute(this)
        }

        while (user.spaceShip == null) {
            spaceShipCreatedCondition.await()
        }
    }

    fun refresh() {
        game.refresh()
    }

    /**
     * Добавляет пользователя в комнату и рассылает уведомление об этом остальным пользователям.
     */
    private fun addUser(user: User) {
        if (users.size >= MAX_USERS) {
            RoomService.getNotFullRoom().addUser(user)
        }
        RoomService.sendMessageToUsers(this, "User ${user.username} has joined the room.")
        users.add(user)
    }

    companion object {

        private val EXECUTOR_SERVICE = Executors.newCachedThreadPool()

        private const val SCREEN_WIDTH = 30

        private const val SCREEN_HEIGHT = 30

        private const val NUMBER_OF_GARBAGE_CELLS = 150

        private const val NUMBER_OF_ASTEROID_CELLS = 50

        private const val MAX_USERS = 1

        private val log = LoggerFactory.getLogger(Room::class.java)
    }

    /**
     * Статус комнаты.
     */
    private enum class Status {
        WAITING_CONNECTIONS,
        GAMING,
        FINISHED,
    }
}