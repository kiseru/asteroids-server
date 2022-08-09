package com.kiseru.asteroids.server.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.ServerSocket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class AppConfig {

    @Bean
    fun serverSocket(@Value("\${asteroids.server.port}") port: Int): ServerSocket = ServerSocket(port)

    @Bean
    fun mainExecutorService(): ExecutorService = Executors.newCachedThreadPool()
}