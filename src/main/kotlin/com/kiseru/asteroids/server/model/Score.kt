package com.kiseru.asteroids.server.model

data class Score(val value: Int) {

    fun increaseBy(points: Int): Score {
        require(points > 0) { "points must be positive" }
        return Score(value + points)
    }

    fun decreaseBy(points: Int): Score {
        require(points > 0) { "points must be positive" }
        return Score(value - points)
    }
}
