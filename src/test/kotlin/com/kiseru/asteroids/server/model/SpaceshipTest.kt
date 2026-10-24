package com.kiseru.asteroids.server.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpaceshipTest {

    @Test
    fun `test default values after creation`() {
        // given
        val user = User(1, "Some cool name")
        val spaceship = Spaceship(Position(1, 1), user)

        // when & then
        assertEquals(Direction.UP, spaceship.direction)
        assertEquals(0, spaceship.steps)
        assertEquals(100, spaceship.score)
        assertTrue(spaceship.isAlive)
    }

    @Test
    fun `test position`() {
        // given
        val user = User(1, "Some cool name")
        val spaceship = Spaceship(Position(1, 1), user)

        // when
        val actual = spaceship.position

        // then
        assertEquals(Position(1, 1), actual)
    }

    @Test
    fun `test type`() {
        // given
        val user = User(1, "Some cool name")
        val spaceship = Spaceship(Position(1, 1), user)

        // when
        val actual = spaceship.type

        // then
        assertEquals(Type.SPACESHIP, actual)
    }

    @Test
    fun `test view`() {
        // given
        val user = User(1, "Some cool name")
        val spaceship = Spaceship(Position(1, 1), user)

        // when
        val actual = spaceship.view()

        // then
        assertEquals("1", actual)
    }

    @Test
    fun `test addScore`() {
        // given
        val user = User(1, "Some cool name")
        val spaceship = Spaceship(Position(1, 1), user)

        // when
        spaceship.addScore()

        // then
        assertEquals(110, spaceship.score)
    }

    @ParameterizedTest
    @CsvSource(
        "50,0,true",
        "49,-1,false",
    )
    fun `test subtractScore`(initialScore: Int, expectedScore: Int, expectedIsAlive: Boolean) {
        // given
        val user = User(1, "Some cool name")
        val spaceship = Spaceship(Position(1, 1), user)
        spaceship.score = initialScore

        // when
        spaceship.subtractScore()

        // then
        assertEquals(expectedScore, spaceship.score)
        assertEquals(expectedIsAlive, spaceship.isAlive)
    }
}