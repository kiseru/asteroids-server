package com.kiseru.asteroids.server.service

import kotlinx.coroutines.flow.Flow

interface MessageReceiverService {

    suspend fun receive(): String

    fun receivingFlow(): Flow<String>
}
