package com.kiseru.asteroids.server.model

class Spaceship(x: Int, y: Int, val user: User) : GameObject(x, y) {

    var direction = Direction.UP
    var steps = 0
    var score = 100
    var isAlive = true

    override val type: Type = Type.SPACESHIP

    override fun view(): String =
        user.id.toString()

    fun addScore() {
        score += 10
    }

    fun subtractScore() {
        score -= 50
        isAlive = score >= 0
    }
}
