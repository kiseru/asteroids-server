package com.kiseru.asteroids.server.factory

import com.kiseru.asteroids.server.logics.Game
import com.kiseru.asteroids.server.model.Screen

interface GameFactory {

    fun createGame(screen: Screen): Game
}