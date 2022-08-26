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
        .let { spaceship.checkContaining(it) }

    override fun isGarbage(): Boolean = pointsOnMap.filter { it.type == Type.GARBAGE }
        .let { spaceship.checkContaining(it) }

    override fun isWall(): Boolean = spaceship.isWall(screen)
}