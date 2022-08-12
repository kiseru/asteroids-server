package com.kiseru.asteroids.server.config

import com.kiseru.asteroids.server.properties.AsteroidsProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.ServerSocket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class AppConfig {

    @Bean
    fun serverSocket(asteroidsProperties: AsteroidsProperties): ServerSocket =
        ServerSocket(asteroidsProperties.server.port)

    @Bean
    fun mainExecutorService(): ExecutorService = Executors.newCachedThreadPool()
}