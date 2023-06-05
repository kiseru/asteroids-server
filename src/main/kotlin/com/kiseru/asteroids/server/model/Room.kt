package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.service.MessageSenderService
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Комната.
 */
class Room(
    val id: UUID,
    val game: Game,
) {

    var users = emptyList<ApplicationUser>()

    var messageSenderServices = emptyList<MessageSenderService>()

    val rating: String = users.sortedBy { it.score }
        .joinToString("\n") { "${it.username} ${it.score}" }

    val isFull: Boolean
        get() = users.size >= MAX_USERS

    val isGameFinished: Boolean
        get() = status == Status.FINISHED

    var status = Status.CREATED

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

    suspend fun refresh() {
        game.refresh()
    }

    companion object {

        const val MAX_USERS = 1

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
