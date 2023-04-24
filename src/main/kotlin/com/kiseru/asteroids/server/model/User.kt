package com.kiseru.asteroids.server.model

class User(
    val id: String,
    val username: String,
    val room: Room,
) {

    var score = 100

    var isAlive = true

    var steps = 0
}
