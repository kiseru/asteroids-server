package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.logics.auxiliary.Coordinates
import com.kiseru.asteroids.server.logics.auxiliary.Type
import com.kiseru.asteroids.server.logics.models.Point

class Garbage(coordinates: Coordinates) : Point(coordinates) {

    override fun view(): String =
        "G"

    override fun getType(): Type =
        Type.GARBAGE
}
