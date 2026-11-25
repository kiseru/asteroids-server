package ru.kiseru.logics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.kiseru.logics.auxiliary.Position
import ru.kiseru.logics.auxiliary.ScreenSize
import ru.kiseru.logics.models.Asteroid
import ru.kiseru.logics.models.Garbage
import ru.kiseru.logics.models.Spaceship

class ScreenTest {

    @ParameterizedTest
    @CsvSource(
        "10, 5",
        "1, 1",
        "3, 7",
    )
    fun `given width and height - when screen created - then dimensions returned`(width: Int, height: Int) {
        // given
        val screen = Screen(ScreenSize(width, height))

        // when
        val actualSize = screen.size

        // then
        assertEquals(width, actualSize.width())
        assertEquals(height, actualSize.height())
    }

    @ParameterizedTest
    @CsvSource(
        "3, 2",
        "1, 1",
        "4, 3",
    )
    fun `given clear screen - when displayed - then interior cells filled with dots`(width: Int, height: Int) {
        // given
        val screen = Screen(ScreenSize(width, height))

        // when
        val result = screen.display()

        // then
        val row = (1..width).joinToString("") { ".\t" } + "\n"
        val expected = row.repeat(height)
        assertEquals(expected, result)
    }

    @Test
    fun `given visible asteroid and garbage at same position - when drawPoint called - then symbol AG drawn on screen`() {
        // given
        val screen = Screen(ScreenSize(1, 1))

        val position = Position(1, 1)
        val asteroid = Asteroid(position)
        val garbage = Garbage(position)

        // when
        screen.drawPoint(asteroid)
        screen.drawPoint(garbage)

        // then
        val lines = screen.display().split("\n")
        assertEquals("A|G", lines[0].split("\t")[0])
    }

    @Test
    fun `given visible asteroid - when drawPoint called - then symbol A drawn on screen`() {
        // given
        val screen = Screen(ScreenSize(1, 1))
        val asteroid = Asteroid(Position(1, 1))

        // when
        screen.drawPoint(asteroid)

        // then
        val lines = screen.display().split("\n")
        assertEquals("A", lines[0].split("\t")[0])
    }

    @Test
    fun `given invisible asteroid - when drawPoint called - then nothing drawn on screen`() {
        // given
        val screen = Screen(ScreenSize(1, 1))
        val asteroid = Asteroid(Position(1, 1))
        asteroid.crash()

        // when
        screen.drawPoint(asteroid)

        // then
        val lines = screen.display().split("\n")
        assertEquals(".", lines[0].split("\t")[0])
    }


    @Test
    fun `given visible garbage - when drawPoint called - then symbol G drawn on screen`() {
        // given
        val screen = Screen(ScreenSize(1, 1))
        val garbage = Garbage(Position(1, 1))

        // when
        screen.drawPoint(garbage)

        // then
        val lines = screen.display().split("\n")
        assertEquals("G", lines[0].split("\t")[0])
    }

    @Test
    fun `given invisible garbage - when drawPoint called - then nothing drawn on screen`() {
        // given
        val screen = Screen(ScreenSize(1, 1))
        val garbage = Garbage(Position(1, 1))
        garbage.crash()

        // when
        screen.drawPoint(garbage)

        // then
        val lines = screen.display().split("\n")
        assertEquals(".", lines[0].split("\t")[0])
    }

    @Test
    fun `given visible spaceship - when drawPoint called - then owner id drawn on screen`() {
        // given
        val screen = Screen(ScreenSize(1, 1))
        val spaceship = Spaceship(Position(1, 1), UserId(1))

        // when
        screen.drawPoint(spaceship)

        // then
        val lines = screen.display().split("\n")
        assertEquals("1", lines[0].split("\t")[0])
    }

    @Test
    fun `given invisible spaceship - when drawPoint called - then nothing drawn on screen`() {
        // given
        val screen = Screen(ScreenSize(1, 1))
        val spaceship = Spaceship(Position(1, 1), UserId(1))
        spaceship.destroy()

        // when
        screen.drawPoint(spaceship)

        // then
        val lines = screen.display().split("\n")
        assertEquals(".", lines[0].split("\t")[0])
    }
}
