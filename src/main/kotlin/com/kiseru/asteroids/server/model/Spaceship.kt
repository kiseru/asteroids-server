package com.kiseru.asteroids.server.model

class Spaceship(
    override var position: Position,
    val user: User,
) : GameObject {

    var direction = Direction.UP
    var steps = 0

    var score = Score(100)
        private set

    var isAlive = true

    override val type: Type = Type.SPACESHIP

    override fun view(): String =
        user.id.toString()

    fun addScore() {
        score = score.increaseBy(10)
    }

    fun subtractScore() {
        score = score.decreaseBy(50)
        isAlive = score.value >= 0
    }
}
