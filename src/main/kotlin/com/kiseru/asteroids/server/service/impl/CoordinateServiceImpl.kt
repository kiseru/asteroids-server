package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.properties.AsteroidsProperties
import com.kiseru.asteroids.server.service.CoordinateService
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class CoordinateServiceImpl(
    private val asteroidsProperties: AsteroidsProperties,
) : CoordinateService {

    override fun generateCoordinateSequence(): Sequence<Pair<Int, Int>> =
        generateSequence { generateCoordinates(asteroidsProperties.screen.width, asteroidsProperties.screen.height) }

    private fun generateCoordinates(width: Int, height: Int): Pair<Int, Int> =
        Random.nextInt(width) + 1 to Random.nextInt(height) + 1
}