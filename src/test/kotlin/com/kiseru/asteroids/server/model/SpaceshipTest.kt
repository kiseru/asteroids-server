package com.kiseru.asteroids.server.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        assertEquals(Score(100), spaceship.score)
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
        assertEquals(Score(110), spaceship.score)
    }

    @Test
    fun `test subtractScore when it doesn't destroy spaceship`() {
        // given
        val user = User(1, "Some cool name")
        val spaceship = Spaceship(Position(1, 1), user)

        // when
        spaceship.subtractScore()

        // then
        assertEquals(Score(50), spaceship.score)
        assertTrue(spaceship.isAlive)
    }

    @Test
    fun `test subtractScore when it destroys spaceship`() {
        // given
        val user = User(1, "Some cool name")
        val spaceship = Spaceship(Position(1, 1), user)

        // when
        repeat(3) { spaceship.subtractScore() }

        // then
        assertEquals(Score(-50), spaceship.score)
        assertFalse(spaceship.isAlive)
    }
}
