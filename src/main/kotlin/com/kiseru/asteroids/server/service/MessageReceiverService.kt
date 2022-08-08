package com.kiseru.asteroids.server.service

interface MessageReceiverService {

    fun receive(): String
}