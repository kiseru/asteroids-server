package com.kiseru.asteroids.server.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SpaceshipTest {

    @Test
    fun `test type`() {
        // given
        val user = User(1, "Some cool name")
        val spaceship = Spaceship(1, 1, user)

        // when
        val actual = spaceship.type

        // then
        assertEquals(Type.SPACESHIP, actual)
    }

    @Test
    fun `test view`() {
        // given
        val user = User(1, "Some cool name")
        val spaceship = Spaceship(1, 1, user)

        // when
        val actual = spaceship.view()

        // then
        assertEquals("1", actual)
    }
}