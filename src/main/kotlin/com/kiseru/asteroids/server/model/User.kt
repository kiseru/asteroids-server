package com.kiseru.asteroids.server.model

import java.util.UUID

class User(
    val id: UUID,
    val username: String,
    val roomId: UUID,
) {

    var score = 100

    var isAlive = true

    var steps = 0
}
