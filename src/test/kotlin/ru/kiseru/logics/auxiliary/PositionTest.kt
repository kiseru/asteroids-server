package ru.kiseru.logics.auxiliary

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PositionTest {

    @Test
    fun `given x - when x is forbidden - then exception thrown`() {
        // when & then
        assertThrows<IllegalArgumentException> { Position(-1, 1) }
    }

    @Test
    fun `given y - when y is forbidden - then exception thrown`() {
        // when & then
        assertThrows<IllegalArgumentException> { Position(1, -1) }
    }

    @ParameterizedTest
    @CsvSource(
        "0, 0",
        "1, 5",
        "3, 7",
        "10, 2",
    )
    fun `given positions with x and y - when getX and getY called - then stored values returned`(x: Int, y: Int) {
        // given
        val position = Position(x, y)

        // when
        val actualX = position.x()
        val actualY = position.y()

        // then
        assertEquals(x, actualX)
        assertEquals(y, actualY)
    }

    @Test
    fun `given two positions with same x and y - when compared - then they are equal`() {
        // given
        val first = Position(1, 2)
        val second = Position(1, 2)

        // when
        val result = first == second

        // then
        assertTrue(result)
    }

    @Test
    fun `given two positions with different x - when compared - then they are not equal`() {
        // given
        val first = Position(1, 2)
        val second = Position(3, 2)

        // when
        val result = first == second

        // then
        assertFalse(result)
    }

    @Test
    fun `given two positions with different y - when compared - then they are not equal`() {
        // given
        val first = Position(1, 2)
        val second = Position(1, 3)

        // when
        val result = first == second

        // then
        assertFalse(result)
    }

    @Test
    fun `given positions and object of other type - when compared - then they are not equal`() {
        // given
        val position = Position(1, 2)
        val other: Any = "not coordinates"

        // when
        val result = position == other

        // then
        assertFalse(result)
    }
}
