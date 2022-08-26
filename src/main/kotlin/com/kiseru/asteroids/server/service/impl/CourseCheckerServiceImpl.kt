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
        .map { it.x to it.y }
        .let { checkContaining(it) }

    override fun isGarbage(): Boolean = pointsOnMap.filter { it.type == Type.GARBAGE }
        .map { it.x to it.y }
        .let { checkContaining(it) }

    override fun isWall(): Boolean = when (spaceship.direction) {
        Direction.UP -> spaceship.y == 1
        Direction.DOWN -> spaceship.y == screen.height
        Direction.RIGHT -> spaceship.x == screen.width
        Direction.LEFT -> spaceship.x == 1
    }

    private fun checkContaining(coordinates: List<Pair<Int, Int>>): Boolean = when (spaceship.direction) {
        Direction.UP -> coordinates.contains(spaceship.x to spaceship.y - 1)
        Direction.DOWN -> coordinates.contains(spaceship.x to spaceship.y + 1)
        Direction.LEFT -> coordinates.contains(spaceship.x - 1 to spaceship.y)
        Direction.RIGHT -> coordinates.contains(spaceship.x + 1 to spaceship.y)
    }
}