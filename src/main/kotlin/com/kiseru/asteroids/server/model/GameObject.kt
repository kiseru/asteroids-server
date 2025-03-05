package com.kiseru.asteroids.server.model

import java.util.Objects

abstract class GameObject(
    var x: Int,
    var y: Int,
) {

    var isVisible = true
    abstract val type: Type

    abstract fun view(): String

    fun destroy() {
        isVisible = false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other == null) {
            return false
        }

        if (other !is GameObject) {
            return false
        }

        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        return Objects.hash(x, y)
    }
}