package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.logics.Game
import com.kiseru.asteroids.server.service.RoomService
import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Комната.
 */
class Room(
    private val game: Game,
    private val mainExecutorService: ExecutorService,
    private val roomService: RoomService,
) : Runnable {

    val users: MutableList<User> = CopyOnWriteArrayList()

    val isFull: Boolean
        get() = users.size >= MAX_USERS

    val isGameStarted: Boolean
        get() = status == Status.GAMING

    val isGameFinished: Boolean
        get() = status == Status.FINISHED

    private val lock: Lock = ReentrantLock()

    private val endgameCondition = lock.newCondition()

    private val spaceshipCreatedCondition = lock.newCondition()

    private var status = Status.WAITING_CONNECTIONS

    override fun run() {
        roomService.sendMessageToUsers(this, "start")
        status = Status.GAMING
        lock.withLock {
            spaceshipCreatedCondition.signalAll()
        }

        game.refresh()

        lock.withLock {
            while (status != Status.FINISHED) {
                endgameCondition.await()
            }
        }

        val rating = roomService.getRoomRating(this)
        roomService.sendMessageToUsers(this, "finish\n$rating")
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
            roomService.getNotFullRoom()
            mainExecutorService.execute(this)
        }

        while (!user.hasSpaceship()) {
            spaceshipCreatedCondition.await()
        }
    }

    fun refresh() {
        game.refresh()
    }

    fun showGameField() {
        game.showField()
    }

    fun incrementCollectedGarbageCount(): Int {
        return game.incrementCollectedGarbageCount()
    }

    /**
     * Добавляет пользователя в комнату и рассылает уведомление об этом остальным пользователям.
     */
    private fun addUser(user: User) {
        if (users.size >= MAX_USERS) {
            roomService.getNotFullRoom().addUser(user)
        }
        roomService.sendMessageToUsers(this, "User ${user.username} has joined the room.")
        game.registerSpaceshipForUser(user)
        users.add(user)
    }

    companion object {

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