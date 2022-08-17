package com.kiseru.asteroids.server.config

import com.kiseru.asteroids.server.properties.AsteroidsProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.ServerSocket

@Configuration
class AppConfig {

    @Bean
    fun serverSocket(asteroidsProperties: AsteroidsProperties): ServerSocket =
        ServerSocket(asteroidsProperties.server.port)
}