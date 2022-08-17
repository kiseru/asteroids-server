package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.logics.Game
import com.kiseru.asteroids.server.service.RoomService
import kotlinx.coroutines.yield
import org.slf4j.LoggerFactory

/**
 * Комната.
 */
class Room(
    private val game: Game,
    private val roomService: RoomService,
) {

    var users = emptyList<User>()
        private set

    val isFull: Boolean
        get() = users.size >= MAX_USERS

    val isGameFinished: Boolean
        get() = status == Status.FINISHED

    private var status = Status.WAITING_CONNECTIONS

    suspend fun run() {
        roomService.sendMessageToUsers(this, "start")
        status = Status.GAMING
        game.refresh()
        awaitEndgame()
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
    fun setGameFinished() {
        if (status == Status.FINISHED) {
            return
        }

        status = Status.FINISHED
        log.info("Game finished")
    }

    fun addUserToRoom(user: User) {
        addUser(user)
        if (isFull) {
            roomService.getNotFullRoom()
        }
    }

    suspend fun start() {
        roomService.startRoom(this)
    }

    suspend fun awaitCreatingSpaceship(user: User) {
        while (!user.hasSpaceship()) {
            yield()
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
        users = users + user
    }

    private suspend fun awaitEndgame() {
        while (status != Status.FINISHED) {
            yield()
        }
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