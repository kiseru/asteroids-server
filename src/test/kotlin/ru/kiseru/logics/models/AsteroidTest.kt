package ru.kiseru.logics.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kiseru.logics.auxiliary.Position
import ru.kiseru.logics.auxiliary.Type

class AsteroidTest {

    @Test
    fun `given coordinates - when asteroid created - then it is visible and stores coordinates`() {
        // given
        val position = Position(3, 7)

        // when
        val asteroid = Asteroid(position)

        // then
        assertEquals(position, asteroid.position)
        assertTrue(asteroid.isVisible)
    }

    @Test
    fun `given asteroid - when getType called - then ASTEROID type returned`() {
        // given
        val asteroid = Asteroid(Position(1, 2))

        // when
        val actual = asteroid.type

        // then
        assertEquals(Type.ASTEROID, actual)
    }

    @Test
    fun `given visible asteroid - when crash called - then asteroid becomes invisible`() {
        // given
        val asteroid = Asteroid(Position(0, 0))
        assertTrue(asteroid.isVisible)

        // when
        asteroid.crash()

        // then
        assertFalse(asteroid.isVisible)
    }
}
