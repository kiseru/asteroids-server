package com.kiseru.asteroids.server.model

import kotlinx.coroutines.yield
import org.slf4j.LoggerFactory

/**
 * Комната.
 */
class Room(
    private val game: Game,
) {

    var users = emptyList<User>()
        private set

    val rating: String = users.sortedBy { it.score }
        .joinToString("\n") { "${it.username} ${it.score}" }

    val isFull: Boolean
        get() = users.size >= MAX_USERS

    val isGameFinished: Boolean
        get() = status == Status.FINISHED

    var status = Status.CREATED

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

    fun addUser(user: User) {
        check(users.size < MAX_USERS)
        status = Status.WAITING_CONNECTIONS
        game.registerSpaceshipForUser(user)
        users = users + user
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

    suspend fun awaitEndgame() {
        while (status != Status.FINISHED) {
            yield()
        }
    }

    suspend fun awaitUsers() {
        while (users.count() < MAX_USERS) {
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
    enum class Status {
        CREATED,
        WAITING_CONNECTIONS,
        GAMING,
        FINISHED,
    }
}