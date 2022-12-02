package com.kiseru.asteroids.server.service

interface CoordinateService {

    fun generateCoordinateSequence(): Sequence<Pair<Int, Int>>
}