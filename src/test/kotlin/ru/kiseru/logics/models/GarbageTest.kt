package ru.kiseru.logics.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kiseru.logics.auxiliary.Position
import ru.kiseru.logics.auxiliary.Type

class GarbageTest {

    @Test
    fun `given coordinates - when garbage created - then it is visible and stores coordinates`() {
        // given
        val position = Position(3, 7)

        // when
        val garbage = Garbage(position)

        // then
        assertEquals(position, garbage.position)
        assertTrue(garbage.isVisible)
    }

    @Test
    fun `given garbage - when getType called - then GARBAGE type returned`() {
        // given
        val garbage = Garbage(Position(1, 2))

        // when
        val actual = garbage.type

        // then
        assertEquals(Type.GARBAGE, actual)
    }

    @Test
    fun `given visible garbage - when crash called - then garbage becomes invisible`() {
        // given
        val garbage = Garbage(Position(0, 0))
        assertTrue(garbage.isVisible)

        // when
        garbage.crash()

        // then
        assertFalse(garbage.isVisible)
    }
}
