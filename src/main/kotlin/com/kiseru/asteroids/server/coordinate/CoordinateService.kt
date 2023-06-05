package com.kiseru.asteroids.server.coordinate

import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Point

interface CoordinateService {

    fun generateUniqueRandomCoordinates(game: Game): Pair<Int, Int>

    fun generateUniqueRandomCoordinates(pointsOnScreen: MutableList<Point>): Pair<Int, Int>
}
