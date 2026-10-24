package com.kiseru.asteroids.server.model

interface GameObject {

    val position: Position

    val type: Type

    fun view(): String
}