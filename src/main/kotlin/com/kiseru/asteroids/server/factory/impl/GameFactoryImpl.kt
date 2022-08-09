package com.kiseru.asteroids.server.factory.impl

import com.kiseru.asteroids.server.factory.GameFactory
import com.kiseru.asteroids.server.logics.Game
import com.kiseru.asteroids.server.model.Screen
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GameFactoryImpl(
    @Value("\${asteroids.number-of-asteroid-cells}") private val numberOfAsteroidCells: Int,
    @Value("\${asteroids.number-of-garbage-cells}") private val numberOfGarbageCells: Int,
) : GameFactory {

    override fun createGame(screen: Screen): Game = Game(screen, numberOfGarbageCells, numberOfAsteroidCells)
}