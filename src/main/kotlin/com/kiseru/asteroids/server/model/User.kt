package com.kiseru.asteroids.server.model

import java.util.UUID

class User(
    val id: UUID,
    val username: String,
) {

    var score = 100

    var isAlive = true

    var steps = 0
}
