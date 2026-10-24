package com.kiseru.asteroids.server.model

class Spaceship(
    position: Position,
    val user: User,
) : GameObject {

    override var position: Position = position
        private set

    var direction = Direction.UP
        private set

    var steps = 0
        private set

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

    fun changeDirection(direction: Direction) {
        ensureAlive()

        this.direction = direction
    }

    fun incrementSteps() {
        ensureAlive()

        if (steps < 1500) {
            steps++
        } else {
            destroy()
        }
    }

    fun moveTo(position: Position) {
        ensureAlive()

        this.position = position
    }

    private fun ensureAlive() {
        check(status == Status.ALIVE) { "Spaceship isn't alive" }
    }

    enum class Status {
        ALIVE,
        DESTROYED,
    }
}
