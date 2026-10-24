package com.kiseru.asteroids.server.model

data class FieldSize(
    val width: Int,
    val height: Int,
) {

    val capacity: Long
        get() = width.toLong() * height

    init {
        require(width > 0) { "width must be positive" }
        require(height > 0) { "height must be positive" }
    }

    fun contains(position: Position): Boolean =
        position.x in 1..width
            && position.y in 1..height
}
