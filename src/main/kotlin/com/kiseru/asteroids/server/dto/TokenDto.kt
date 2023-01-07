package com.kiseru.asteroids.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenDto(
    var token: String,
)