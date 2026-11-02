package ru.kiseru.logics.auxiliary

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CoordinatesTest {

    @ParameterizedTest
    @CsvSource(
        "0, 0",
        "1, 5",
        "-3, 7",
        "10, -2",
    )
    fun `given coordinates with x and y - when getX and getY called - then stored values returned`(x: Int, y: Int) {
        // given
        val coordinates = Coordinates(x, y)

        // when
        val actualX = coordinates.x
        val actualY = coordinates.y

        // then
        assertEquals(x, actualX)
        assertEquals(y, actualY)
    }

    @Test
    fun `given two coordinates with same x and y - when compared - then they are equal`() {
        // given
        val first = Coordinates(1, 2)
        val second = Coordinates(1, 2)

        // when
        val result = first == second

        // then
        assertTrue(result)
    }

    @Test
    fun `given two coordinates with different x - when compared - then they are not equal`() {
        // given
        val first = Coordinates(1, 2)
        val second = Coordinates(3, 2)

        // when
        val result = first == second

        // then
        assertFalse(result)
    }

    @Test
    fun `given two coordinates with different y - when compared - then they are not equal`() {
        // given
        val first = Coordinates(1, 2)
        val second = Coordinates(1, -2)

        // when
        val result = first == second

        // then
        assertFalse(result)
    }

    @Test
    fun `given coordinates and object of other type - when compared - then they are not equal`() {
        // given
        val coordinates = Coordinates(1, 2)
        val other: Any = "not coordinates"

        // when
        val result = coordinates == other

        // then
        assertFalse(result)
    }
}
