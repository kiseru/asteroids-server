package com.kiseru.asteroids.server.model

data class GameField(
    val width: Int,
    val height: Int,
    val objects: List<GameObject> = emptyList(),
)