package com.kiseru.asteroids.server.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.impl.JWTParser
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.TokenService
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class TokenServiceImpl(
    private val algorithm: Algorithm,
    private val verifier: JWTVerifier,
) : TokenService {

    override fun generateToken(user: User): String = Instant.now().let {
        JWT.create()
            .withSubject(user.id.toString())
            .withIssuedAt(it)
            .withExpiresAt(it.plus(Duration.ofMinutes(15)))
            .sign(algorithm)
    }

    override fun getUserId(token: String): UUID {
        val decodedJWT = verifier.verify(token)
        return UUID.fromString(decodedJWT.subject)
    }
}
