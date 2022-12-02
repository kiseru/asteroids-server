package com.kiseru.asteroids.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("com.kiseru.asteroids.server.properties")
class AsteroidsServerApplication

suspend fun main(args: Array<String>) {
    val context = runApplication<AsteroidsServerApplication>(*args)
    val server = context.getBean(Server::class.java)
    server.startServer()
}