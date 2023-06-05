package com.kiseru.asteroids.server.spaceship

import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.User

interface SpaceshipService {

    suspend fun createSpaceship(user: User, room: Room, game: Game): Spaceship
}
