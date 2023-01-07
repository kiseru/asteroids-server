package com.kiseru.asteroids.server.dto

import com.kiseru.asteroids.server.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val username: String,
) {

    constructor(user: User) : this(user.id, user.username)
}