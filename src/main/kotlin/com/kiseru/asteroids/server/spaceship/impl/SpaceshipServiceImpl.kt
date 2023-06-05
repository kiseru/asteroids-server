package com.kiseru.asteroids.server.spaceship.impl

import com.kiseru.asteroids.server.coordinate.CoordinateService
import com.kiseru.asteroids.server.model.ApplicationUser
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.service.impl.CourseCheckerServiceImpl
import com.kiseru.asteroids.server.spaceship.SpaceshipService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Service
import java.util.*

@Service
class SpaceshipServiceImpl(private val coordinateService: CoordinateService) : SpaceshipService {

    private val spaceshipStorage = mutableMapOf<UUID, Spaceship>()

    private val spaceshipStorageMutex = Mutex()

    override suspend fun createSpaceship(user: ApplicationUser, room: Room, game: Game): Spaceship =
        spaceshipStorageMutex.withLock {
            val courseCheckerService = CourseCheckerServiceImpl(game.pointsOnScreen, game.screen)
            val (x, y) = coordinateService.generateUniqueRandomCoordinates(game)
            val spaceshipId = generateUniqueSpaceshipId()
            val spaceship = Spaceship(spaceshipId, user, room, courseCheckerService, x, y)
            spaceshipStorage[spaceshipId] = spaceship
            user.spaceshipId = spaceshipId
            courseCheckerService.spaceship = spaceship
            spaceship
        }

    private fun generateUniqueSpaceshipId(): UUID =
        generateSequence { UUID.randomUUID() }
            .dropWhile { spaceshipStorage.containsKey(it) }
            .first()

    override fun findSpaceshipById(spaceshipId: UUID): Spaceship? =
        spaceshipStorage[spaceshipId]
}
