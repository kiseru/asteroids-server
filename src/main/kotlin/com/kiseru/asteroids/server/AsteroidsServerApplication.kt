package com.kiseru.asteroids.server

import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AsteroidsServerApplication

fun main(args: Array<String>) = runBlocking {
    val context = runApplication<AsteroidsServerApplication>(*args)
    val server = context.getBean(Server::class.java)
    server.startServer()
}