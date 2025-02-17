package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.logics.auxiliary.Coordinates
import com.kiseru.asteroids.server.logics.auxiliary.Type
import com.kiseru.asteroids.server.logics.models.Point

class Asteroid(coordinates: Coordinates) : Point(coordinates) {

    override fun getType(): Type =
        Type.ASTEROID

    override fun view(): String =
        "A"
}
