package com.kiseru.asteroids.server.model

abstract class Point protected constructor(var coordinates: Coordinates) {

    var isVisible = true
        protected set

    val x: Int
        get() = coordinates.x

    val y: Int
        get() = coordinates.y

    abstract val type: Type

    abstract val symbolToShow: String

    open fun destroy() {
        isVisible = false
    }

    fun render(screen: Screen) {
        if (isVisible) {
            screen.draw(coordinates, this)
        }
    }
}
