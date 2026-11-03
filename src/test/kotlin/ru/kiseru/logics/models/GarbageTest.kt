package ru.kiseru.logics.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kiseru.logics.Screen
import ru.kiseru.logics.auxiliary.Coordinates
import ru.kiseru.logics.auxiliary.Type

class GarbageTest {

    @Test
    fun `given coordinates - when garbage created - then it is visible and stores coordinates`() {
        // given
        val coordinates = Coordinates(3, 7)

        // when
        val garbage = Garbage(coordinates)

        // then
        assertEquals(coordinates, garbage.coordinates)
        assertTrue(garbage.isVisible)
    }

    @Test
    fun `given garbage - when getType called - then GARBAGE type returned`() {
        // given
        val garbage = Garbage(Coordinates(1, 2))

        // when
        val actual = garbage.type

        // then
        assertEquals(Type.GARBAGE, actual)
    }

    @Test
    fun `given visible garbage - when render called - then symbol G drawn on screen`() {
        // given
        val screen = Screen(5, 5)
        val garbage = Garbage(Coordinates(2, 3))

        // when
        garbage.render(screen)

        // then
        val lines = screen.display().split("\n")
        assertEquals("G", lines[2].split("\t")[1])
    }

    @Test
    fun `given crashed garbage - when render called - then nothing drawn on screen`() {
        // given
        val screen = Screen(5, 5)
        val garbage = Garbage(Coordinates(2, 3))
        garbage.crash()
        assertFalse(garbage.isVisible)

        // when
        garbage.render(screen)

        // then
        val lines = screen.display().split("\n")
        assertEquals(".", lines[2].split("\t")[1])
    }

    @Test
    fun `given visible garbage - when crash called - then garbage becomes invisible`() {
        // given
        val garbage = Garbage(Coordinates(0, 0))
        assertTrue(garbage.isVisible)

        // when
        garbage.crash()

        // then
        assertFalse(garbage.isVisible)
    }
}
