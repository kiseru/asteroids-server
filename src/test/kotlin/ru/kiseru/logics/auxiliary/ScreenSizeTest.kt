package ru.kiseru.logics.auxiliary

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ScreenSizeTest {

    @Test
    fun `given height - when height is forbidden - then exception thrown`() {
        // when & then
        assertThrows<IllegalArgumentException> { ScreenSize(10, 0) }
    }

    @Test
    fun `given width - when width is forbidden - then exception thrown`() {
        // when & then
        assertThrows<IllegalArgumentException> { ScreenSize(0, 10) }
    }

    @Test
    fun `given width and height - when screen size created - then stored values returned`() {
        // given
        val width = 10
        val height = 5

        // when
        val actual = ScreenSize(width, height)

        // then
        assertEquals(width, actual.width())
        assertEquals(height, actual.height())
    }

    @ParameterizedTest
    @CsvSource(
        "1, 1",
        "1, 5",
        "10, 1",
        "10, 5",
        "5, 3",
    )
    fun `given position inside screen - when isInBounds called - then true returned`(x: Int, y: Int) {
        // given
        val screenSize = ScreenSize(10, 5)
        val position = Position(x, y)

        // when
        val actual = screenSize.isInBounds(position)

        // then
        assertTrue(actual)
    }

    @ParameterizedTest
    @CsvSource(
        "0, 1",
        "0, 5",
        "1, 0",
        "10, 0",
        "11, 1",
        "11, 5",
        "1, 6",
        "10, 6",
        "11, 6",
    )
    fun `given position outside screen - when isInBounds called - then false returned`(x: Int, y: Int) {
        // given
        val screenSize = ScreenSize(10, 5)
        val position = Position(x, y)

        // when
        val actual = screenSize.isInBounds(position)

        // then
        assertFalse(actual)
    }
}
