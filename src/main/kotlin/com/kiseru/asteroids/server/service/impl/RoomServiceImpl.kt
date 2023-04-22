package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.factory.GameFactory
import com.kiseru.asteroids.server.factory.ScreenFactory
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.service.RoomService
import kotlinx.coroutines.yield
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
class RoomServiceImpl(
    private val gameFactory: GameFactory,
    private val screenFactory: ScreenFactory,
) : RoomService {

    private val rooms = mutableListOf<Room>()

    private var notFullRoom = createRoom()

    private val lock = ReentrantLock()

    override fun getNotFullRoom(): Room {
        lock.withLock {
            if (!notFullRoom.isFull) {
                return notFullRoom
            }

            rooms.add(notFullRoom)
            notFullRoom = createRoom()
            return notFullRoom
        }
    }

    /**
     * Рассылает сообщение пользователям комнаты.
     *
     * @param message сообщение
     */
    override suspend fun sendMessageToUsers(room: Room, message: String) {
        for (messageSenderService in room.messageSenderServices) {
            messageSenderService.send(message)
        }
    }

    override fun showAllRatings() {
        for (room in rooms) {
            println(room.rating)
        }
    }

    override fun showAllGameFields() {
        for (room in rooms) {
            room.game.showField()
        }
    }

    override suspend fun startRoom(room: Room) {
        sendMessageToUsers(room, "start")
        room.status = Room.Status.GAMING
        room.refresh()
        awaitEndgame(room)
        val rating = room.rating
        sendMessageToUsers(room, "finish\n$rating")
        log.info("Room released! Rating table:\n$rating")
    }

    override suspend fun awaitEndgame(room: Room) {
        while (room.status != Room.Status.FINISHED) {
            yield()
        }
    }

    private fun createRoom() = Room(createGame())

    private fun createGame(): Game = gameFactory.createGame(screenFactory.createScreen())

    companion object {

        private val log = LoggerFactory.getLogger(RoomServiceImpl::class.java)
    }
}