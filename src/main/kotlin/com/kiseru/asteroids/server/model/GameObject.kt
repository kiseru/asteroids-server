package com.kiseru.asteroids.server.model

interface GameObject {

    val x: Int

    val y: Int

    val type: Type

    fun view(): String
}