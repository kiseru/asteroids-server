package com.kiseru.asteroids.server.game

import com.kiseru.asteroids.server.model.*

interface GameService {

    suspend fun registerSpaceshipForUser(game: Game, user: User, room: Room)

    suspend fun createGame(): Game
}
