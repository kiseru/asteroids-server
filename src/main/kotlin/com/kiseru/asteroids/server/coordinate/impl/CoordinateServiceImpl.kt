package com.kiseru.asteroids.server.coordinate.impl

import com.kiseru.asteroids.server.coordinate.CoordinateService
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Point
import com.kiseru.asteroids.server.properties.AsteroidsProperties
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class CoordinateServiceImpl(private val asteroidsProperties: AsteroidsProperties) : CoordinateService {

    override fun generateUniqueRandomCoordinates(game: Game): Pair<Int, Int> = generateCoordinateSequence()
        .dropWhile { isGameObjectsContainsCoordinates(game, it.first, it.second) }
        .first()

    override fun generateUniqueRandomCoordinates(pointsOnScreen: MutableList<Point>): Pair<Int, Int> =
        generateCoordinateSequence()
            .dropWhile { isGameObjectsContainsCoordinates(it.first, it.second, pointsOnScreen) }
            .first()

    private fun generateCoordinateSequence(): Sequence<Pair<Int, Int>> =
        generateSequence { generateCoordinates(asteroidsProperties.screen.width, asteroidsProperties.screen.height) }

    private fun generateCoordinates(width: Int, height: Int): Pair<Int, Int> =
        Random.nextInt(width) + 1 to Random.nextInt(height) + 1

    private fun isGameObjectsContainsCoordinates(game: Game, x: Int, y: Int): Boolean =
        game.pointsOnScreen.any { it.x == x && it.y == y }

    private fun isGameObjectsContainsCoordinates(x: Int, y: Int, pointsOnScreen: MutableList<Point>): Boolean =
        pointsOnScreen.any { it.x == x && it.y == y }
}
