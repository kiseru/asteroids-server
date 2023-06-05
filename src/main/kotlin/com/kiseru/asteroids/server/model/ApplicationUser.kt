package com.kiseru.asteroids.server.model

import java.util.UUID

class ApplicationUser(
    val id: UUID,
    val username: String,
    val roomId: UUID,
) {

    lateinit var spaceshipId: UUID

    var score = 100

    var isAlive = true

    var steps = 0
}
