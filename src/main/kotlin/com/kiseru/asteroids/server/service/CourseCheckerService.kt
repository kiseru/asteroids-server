package com.kiseru.asteroids.server.service

interface CourseCheckerService {

    fun isAsteroid(): Boolean
    fun isGarbage(): Boolean
    fun isWall(): Boolean
}
