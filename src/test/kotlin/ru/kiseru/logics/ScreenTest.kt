package ru.kiseru.logics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.kiseru.logics.auxiliary.Coordinates

class ScreenTest {

    @ParameterizedTest
    @CsvSource(
        "10, 5",
        "1, 1",
        "3, 7",
    )
    fun `given width and height - when screen created - then dimensions returned`(width: Int, height: Int) {
        // given
        val screen = Screen(width, height)

        // when
        val actualWidth = screen.width
        val actualHeight = screen.height

        // then
        assertEquals(width, actualWidth)
        assertEquals(height, actualHeight)
    }

    @ParameterizedTest
    @CsvSource(
        "3, 2",
        "1, 1",
        "4, 3",
    )
    fun `given clear screen - when displayed - then interior cells filled with dots`(width: Int, height: Int) {
        // given
        val screen = Screen(width, height)

        // when
        val result = screen.display()

        // then
        val row = (1..width).joinToString("") { ".\t" } + "\n"
        val expected = row.repeat(height)
        assertEquals(expected, result)
    }

    @Test
    fun `given clear screen - when symbol drawn on empty cell - then cell contains symbol`() {
        // given
        val screen = Screen(3, 3)
        val coordinates = Coordinates(2, 1)

        // when
        screen.draw(coordinates, "A")

        // then
        val expected = """
            .	A	.	
            .	.	.	
            .	.	.	
        """.trimIndent() + "\n"
        assertEquals(expected, screen.display())
    }

    @Test
    fun `given screen with symbol on cell - when another symbol drawn on same cell - then symbols joined with separator`() {
        // given
        val screen = Screen(3, 3)
        val coordinates = Coordinates(1, 2)
        screen.draw(coordinates, "A")

        // when
        screen.draw(coordinates, "B")

        // then
        val expected = """
            .	.	.	
            A|B	.	.	
            .	.	.	
        """.trimIndent() + "\n"
        assertEquals(expected, screen.display())
    }

    @Test
    fun `given clear screen - when symbols drawn on cells with same x and different y - then symbols rendered in different rows`() {
        // given
        val screen = Screen(3, 3)
        val first = Coordinates(1, 1)
        val second = Coordinates(1, 3)

        // when
        screen.draw(first, "A")
        screen.draw(second, "B")

        // then
        val expected = """
            A	.	.	
            .	.	.	
            B	.	.	
        """.trimIndent() + "\n"
        assertEquals(expected, screen.display())
    }

    @Test
    fun `given screen with drawn symbol - when updated - then screen cleared`() {
        // given
        val screen = Screen(3, 3)
        screen.draw(Coordinates(2, 1), "A")

        // when
        screen.update()

        // then
        val expected = """
            .	.	.	
            .	.	.	
            .	.	.	
        """.trimIndent() + "\n"
        assertEquals(expected, screen.display())
    }

    @ParameterizedTest
    @CsvSource(
        "3, 3, 5, 1",
        "3, 3, 1, 5",
        "3, 3, -1, 1",
        "3, 3, 1, -1",
    )
    fun `given screen - when drawn out of bounds - then exception thrown`(width: Int, height: Int, x: Int, y: Int) {
        // given
        val screen = Screen(width, height)
        val coordinates = Coordinates(x, y)

        // when & then
        assertThrows<IndexOutOfBoundsException> { screen.draw(coordinates, "A") }
    }
}
