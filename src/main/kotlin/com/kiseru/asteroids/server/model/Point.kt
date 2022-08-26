package com.kiseru.asteroids.server.model

abstract class Point protected constructor(var x: Int, var y: Int) {

    var isVisible = true
        protected set

    abstract val type: Type

    abstract val symbolToShow: String

    open fun destroy() {
        isVisible = false
    }

    fun render(screen: Screen) {
        if (isVisible) {
            screen.draw(this)
        }
    }
}
