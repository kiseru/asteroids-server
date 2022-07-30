package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.Screen
import com.kiseru.asteroids.server.model.*

class CourseCheckerService(
    private val spaceShip: SpaceShip,
    private val pointsOnMap: List<Point>,
    private val screen: Screen,
) {

    fun isAsteroid(): Boolean = pointsOnMap.filter { it.type == Type.ASTEROID }
        .map { it.coordinates }
        .let { checkContaining(it) }

    fun isGarbage(): Boolean = pointsOnMap.filter { it.type == Type.GARBAGE }
        .map { it.coordinates }
        .let { checkContaining(it) }

    fun isWall(): Boolean = when (spaceShip.direction) {
        Direction.UP -> spaceShip.y == 1
        Direction.DOWN -> spaceShip.y == screen.height
        Direction.RIGHT -> spaceShip.x == screen.width
        Direction.LEFT -> spaceShip.x == 1
        else -> throw IllegalStateException()
    }

    private fun checkContaining(coordinates: List<Coordinates>): Boolean = when (spaceShip.direction) {
        Direction.UP -> coordinates.contains(Coordinates(spaceShip.x, spaceShip.y - 1))
        Direction.DOWN -> coordinates.contains(Coordinates(spaceShip.x, spaceShip.y + 1))
        Direction.LEFT -> coordinates.contains(Coordinates(spaceShip.x - 1, spaceShip.y))
        Direction.RIGHT -> coordinates.contains(Coordinates(spaceShip.x + 1, spaceShip.y))
        else -> throw IllegalStateException()
    }
}