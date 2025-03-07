package com.kiseru.asteroids.server.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AsteroidTest {

    @Test
    fun `test type`() {
        // given
        val asteroid = Asteroid(1, 1)

        // when
        val actual = asteroid.type

        // then
        assertEquals(Type.ASTEROID, actual)
    }

    @Test
    fun `test view`() {
        // given
        val asteroid = Asteroid(1, 1)

        // when
        val actual = asteroid.view()

        // then
        assertEquals("A", actual)
    }
}