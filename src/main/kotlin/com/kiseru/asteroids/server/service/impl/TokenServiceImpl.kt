package com.kiseru.asteroids.server.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.TokenService
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class TokenServiceImpl(
    private val algorithm: Algorithm,
) : TokenService {

    override fun generateToken(user: User): String = Instant.now().let {
        JWT.create()
            .withSubject(user.id)
            .withIssuedAt(it)
            .withExpiresAt(it.plus(Duration.ofMinutes(15)))
            .sign(algorithm)
    }
}