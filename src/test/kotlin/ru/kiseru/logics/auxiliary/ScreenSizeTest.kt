package ru.kiseru.logics.auxiliary

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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
}
