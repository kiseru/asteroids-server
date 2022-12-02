package com.kiseru.asteroids.server.service

interface MessageReceiverService {

    suspend fun receive(): String
}