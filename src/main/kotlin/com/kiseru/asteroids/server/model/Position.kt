package com.kiseru.asteroids.server.model

data class Position(val x: Int, val y: Int) {

    fun aheadOf(direction: Direction): Position =
        when (direction) {
            Direction.UP -> copy(y = y - 1)
            Direction.DOWN -> copy(y = y + 1)
            Direction.LEFT -> copy(x = x - 1)
            Direction.RIGHT -> copy(x = x + 1)
        }
}
