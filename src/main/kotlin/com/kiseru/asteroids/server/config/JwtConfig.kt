package com.kiseru.asteroids.server.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JwtConfig {

    @Bean
    fun jwtVerifier(algorithm: Algorithm): JWTVerifier = JWT.require(algorithm)
        .build()

    @Bean
    fun algorithm(): Algorithm = Algorithm.HMAC256("secret")
}