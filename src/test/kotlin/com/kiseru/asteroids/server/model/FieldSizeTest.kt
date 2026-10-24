package com.kiseru.asteroids.server.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class FieldSizeTest {

    @Test
    fun `test creating`() {
        // when & then
        assertDoesNotThrow { FieldSize(1, 1) }
    }

    @Test
    fun `test creating when width is not positive`() {
        // when & then
        assertThrows<IllegalArgumentException> { FieldSize(0, 1) }
    }

    @Test
    fun `test creating when height is not positive`() {
        // when & then
        assertThrows<IllegalArgumentException> { FieldSize(1, 0) }
    }

    @Test
    fun `test capacity`() {
        // given
        val fieldSize = FieldSize(10, 5)

        // when
        val actual = fieldSize.capacity

        // then
        assertEquals(50, actual)
    }

    @ParameterizedTest
    @CsvSource(
        "1,3",
        "5,3",
        "3,1",
        "3,5"
    )
    fun `test contains when position in field`(x: Int, y: Int) {
        // given
        val fieldSize = FieldSize(5, 5)
        val position = Position(x, y)

        // when
        val actual = fieldSize.contains(position)

        // then
        assertTrue(actual)
    }

    @ParameterizedTest
    @CsvSource(
        "0,3",
        "6,3",
        "3,0",
        "3,6"
    )
    fun `test contains when position not in field`(x: Int, y: Int) {
        // given
        val fieldSize = FieldSize(5, 5)
        val position = Position(x, y)

        // when
        val actual = fieldSize.contains(position)

        // then
        assertFalse(actual)
    }
}
