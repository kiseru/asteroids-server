package com.kiseru.asteroids.server.factory

import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Screen

interface GameFactory {

    fun createGame(screen: Screen): Game
}