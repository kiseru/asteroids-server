package ru.kiseru.logics.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kiseru.logics.Screen
import ru.kiseru.logics.auxiliary.Coordinates
import ru.kiseru.logics.auxiliary.Type

class AsteroidTest {

    @Test
    fun `given coordinates - when asteroid created - then it is visible and stores coordinates`() {
        // given
        val coordinates = Coordinates(3, 7)

        // when
        val asteroid = Asteroid(coordinates)

        // then
        assertEquals(coordinates, asteroid.coordinates)
        assertTrue(asteroid.isVisible)
    }

    @Test
    fun `given asteroid - when getType called - then ASTEROID type returned`() {
        // given
        val asteroid = Asteroid(Coordinates(1, 2))

        // when
        val actual = asteroid.type

        // then
        assertEquals(Type.ASTEROID, actual)
    }

    @Test
    fun `given visible asteroid - when render called - then symbol A drawn on screen`() {
        // given
        val screen = Screen(5, 5)
        val asteroid = Asteroid(Coordinates(2, 3))

        // when
        asteroid.render(screen)

        // then
        val lines = screen.display().split("\n")
        assertEquals("A", lines[2].split("\t")[1])
    }

    @Test
    fun `given crashed asteroid - when render called - then nothing drawn on screen`() {
        // given
        val screen = Screen(5, 5)
        val asteroid = Asteroid(Coordinates(2, 3))
        asteroid.crash()
        assertFalse(asteroid.isVisible)

        // when
        asteroid.render(screen)

        // then
        val lines = screen.display().split("\n")
        assertEquals(".", lines[2].split("\t")[1])
    }

    @Test
    fun `given visible asteroid - when crash called - then asteroid becomes invisible`() {
        // given
        val asteroid = Asteroid(Coordinates(0, 0))
        assertTrue(asteroid.isVisible)

        // when
        asteroid.crash()

        // then
        assertFalse(asteroid.isVisible)
    }
}
