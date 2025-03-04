package com.kiseru.asteroids.server.model

import java.util.Objects

abstract class GameObject(var coordinates: Pair<Int, Int>) {

    var isVisible = true
    val x: Int
        get() = coordinates.first
    val y: Int
        get() = coordinates.second
    abstract val type: Type

    constructor(x: Int, y: Int) : this(x to y)

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