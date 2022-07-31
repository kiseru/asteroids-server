package com.kiseru.asteroids.server.handler

interface CommandHandlerFactory {

    fun create(command: String): CommandHandler
}