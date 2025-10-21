package com.kiseru.asteroids.server.model

data class Spaceship(
    override var x: Int,
    override var y: Int,
    val user: User,
) : GameObject {

    override val type: Type = Type.SPACESHIP

    override fun view(): String =
        user.id.toString()
}
