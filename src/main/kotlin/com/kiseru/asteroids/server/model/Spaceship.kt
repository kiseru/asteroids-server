package com.kiseru.asteroids.server.model

class Spaceship(
    override var position: Position,
    val user: User,
) : GameObject {

    var direction = Direction.UP
    var steps = 0

    var score = Score(100)
        private set

    var status = Status.ALIVE
        private set

    override val type: Type = Type.SPACESHIP

    override fun view(): String =
        user.id.toString()

    fun addScore() {
        ensureAlive()

        score = score.increaseBy(10)
    }

    fun subtractScore() {
        ensureAlive()

        score = score.decreaseBy(50)
        if (score.value < 0) {
            destroy()
        }
    }

    fun destroy() {
        ensureAlive()

        status = Status.DESTROYED
    }

    private fun ensureAlive() {
        check(status == Status.ALIVE) { "Spaceship isn't alive" }
    }

    enum class Status {
        ALIVE,
        DESTROYED,
    }
}
