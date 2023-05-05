package com.kiseru.asteroids.server.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.impl.TokenServiceImpl
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class TokenServiceTest {

    private val algorithm = Algorithm.HMAC256("secret")

    private val verifier = JWT.require(algorithm)
        .build()

    private val underTest: TokenService = TokenServiceImpl(algorithm, verifier)

    private lateinit var closeable: AutoCloseable

    @BeforeEach
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @AfterEach
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun `test generating token`() {
        val user = User(UUID.randomUUID(), "username")

        val actual = underTest.generateToken(user)

        val decodedJwt = verifier.verify(actual)
        assertThat(UUID.fromString(decodedJwt.subject)).isEqualTo(user.id)
        assertThat(decodedJwt.issuedAt).isBefore(decodedJwt.expiresAt)
        assertThat(decodedJwt.expiresAt).isEqualTo(decodedJwt.issuedAt.toInstant().plus(15, ChronoUnit.MINUTES))
    }

    @Test
    fun `test getUserId while token is expired`() {
        val userId = UUID.randomUUID()
        val now = Instant.now()
        val token = JWT.create()
            .withSubject(userId.toString())
            .withIssuedAt(now)
            .withExpiresAt(now.minus(Duration.ofSeconds(1)))
            .sign(algorithm)

        assertThatExceptionOfType(TokenExpiredException::class.java)
            .isThrownBy { underTest.getUserId(token) }
    }

    @Test
    fun `test getUserId`() {
        val userId = UUID.randomUUID()
        val now = Instant.now()
        val token = JWT.create()
            .withSubject(userId.toString())
            .withIssuedAt(now)
            .withExpiresAt(now.plus(Duration.ofMinutes(15)))
            .sign(algorithm)

        val actual = underTest.getUserId(token)

        assertThat(actual).isEqualTo(userId)
    }
}
