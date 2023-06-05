package com.kiseru.asteroids.server.spaceship

import com.kiseru.asteroids.server.model.ApplicationUser
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import java.util.*

interface SpaceshipService {

    suspend fun createSpaceship(user: ApplicationUser, room: Room, game: Game): Spaceship

    fun findSpaceshipById(spaceshipId: UUID): Spaceship?
}
