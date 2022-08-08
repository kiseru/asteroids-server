package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.model.Screen
import com.kiseru.asteroids.server.model.*
import com.kiseru.asteroids.server.service.CourseCheckerService

class CourseCheckerServiceImpl(
    private val pointsOnMap: List<Point>,
    private val screen: Screen,
) : CourseCheckerService {

    lateinit var spaceship: Spaceship

    override fun isAsteroid(): Boolean = pointsOnMap.filter { it.type == Type.ASTEROID }
        .map { it.coordinates }
        .let { checkContaining(it) }

    override fun isGarbage(): Boolean = pointsOnMap.filter { it.type == Type.GARBAGE }
        .map { it.coordinates }
        .let { checkContaining(it) }

    override fun isWall(): Boolean = when (spaceship.direction) {
        Direction.UP -> spaceship.y == 1
        Direction.DOWN -> spaceship.y == screen.height
        Direction.RIGHT -> spaceship.x == screen.width
        Direction.LEFT -> spaceship.x == 1
    }

    private fun checkContaining(coordinates: List<Coordinates>): Boolean = when (spaceship.direction) {
        Direction.UP -> coordinates.contains(Coordinates(spaceship.x, spaceship.y - 1))
        Direction.DOWN -> coordinates.contains(Coordinates(spaceship.x, spaceship.y + 1))
        Direction.LEFT -> coordinates.contains(Coordinates(spaceship.x - 1, spaceship.y))
        Direction.RIGHT -> coordinates.contains(Coordinates(spaceship.x + 1, spaceship.y))
    }
}