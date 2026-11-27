package ru.kiseru.logics

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.kiseru.logics.auxiliary.Position
import ru.kiseru.logics.auxiliary.ScreenSize
import ru.kiseru.logics.auxiliary.Direction
import ru.kiseru.logics.models.Asteroid
import ru.kiseru.logics.models.Garbage
import ru.kiseru.logics.models.Spaceship

class CourseCheckerTest {

    @ParameterizedTest
    @CsvSource(
        "UP",
        "RIGHT",
        "DOWN",
        "LEFT",
    )
    fun `given asteroid on adjacent cell in spaceship direction - when isAsteroid called - then returns true`(direction: Direction) {
        // given
        val spaceship = createSpaceship(direction)
        val asteroidCoordinates = adjacentCoordinates(direction)
        val points = listOf(Asteroid(asteroidCoordinates))
        val courseChecker = CourseChecker(spaceship, points, ScreenSize(10, 10))

        // when
        val result = courseChecker.isAsteroid()

        // then
        assertTrue(result)
    }

    @ParameterizedTest
    @CsvSource(
        "UP",
        "RIGHT",
        "DOWN",
        "LEFT",
    )
    fun `given no asteroid on adjacent cell in spaceship direction - when isAsteroid called - then returns false`(direction: Direction) {
        // given
        val spaceship = createSpaceship(direction)
        val points = listOf(Asteroid(Position(1, 1)))
        val courseChecker = CourseChecker(spaceship, points, ScreenSize(10, 10))

        // when
        val result = courseChecker.isAsteroid()

        // then
        assertFalse(result)
    }

    @Test
    fun `given garbage on adjacent cell in spaceship direction - when isAsteroid called - then returns false`() {
        // given
        val spaceship = createSpaceship(Direction.UP)
        val points = listOf(Garbage(Position(5, 4)))
        val courseChecker = CourseChecker(spaceship, points, ScreenSize(10, 10))

        // when
        val result = courseChecker.isAsteroid()

        // then
        assertFalse(result)
    }

    @ParameterizedTest
    @CsvSource(
        "UP",
        "RIGHT",
        "DOWN",
        "LEFT",
    )
    fun `given garbage on adjacent cell in spaceship direction - when isGarbage called - then returns true`(direction: Direction) {
        // given
        val spaceship = createSpaceship(direction)
        val garbageCoordinates = adjacentCoordinates(direction)
        val points = listOf(Garbage(garbageCoordinates))
        val courseChecker = CourseChecker(spaceship, points, ScreenSize(10, 10))

        // when
        val result = courseChecker.isGarbage()

        // then
        assertTrue(result)
    }

    @ParameterizedTest
    @CsvSource(
        "UP",
        "RIGHT",
        "DOWN",
        "LEFT",
    )
    fun `given no garbage on adjacent cell in spaceship direction - when isGarbage called - then returns false`(direction: Direction) {
        // given
        val spaceship = createSpaceship(direction)
        val points = listOf(Garbage(Position(1, 1)))
        val courseChecker = CourseChecker(spaceship, points, ScreenSize(10, 10))

        // when
        val result = courseChecker.isGarbage()

        // then
        assertFalse(result)
    }

    @Test
    fun `given asteroid on adjacent cell in spaceship direction - when isGarbage called - then returns false`() {
        // given
        val spaceship = createSpaceship(Direction.UP)
        val points = listOf(Asteroid(Position(5, 4)))
        val courseChecker = CourseChecker(spaceship, points, ScreenSize(10, 10))

        // when
        val result = courseChecker.isGarbage()

        // then
        assertFalse(result)
    }

    @Test
    fun `given spaceship moving up on top border - when isWall called - then returns true`() {
        // given
        val spaceship = createSpaceship(Direction.UP, x = 5, y = 1)
        val courseChecker = CourseChecker(spaceship, emptyList(), ScreenSize(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertTrue(result)
    }

    @Test
    fun `given spaceship moving up not on top border - when isWall called - then returns false`() {
        // given
        val spaceship = createSpaceship(Direction.UP, x = 5, y = 5)
        val courseChecker = CourseChecker(spaceship, emptyList(), ScreenSize(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertFalse(result)
    }

    @Test
    fun `given spaceship moving right on right border - when isWall called - then returns true`() {
        // given
        val spaceship = createSpaceship(Direction.RIGHT, x = 10, y = 5)
        val courseChecker = CourseChecker(spaceship, emptyList(), ScreenSize(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertTrue(result)
    }

    @Test
    fun `given spaceship moving right not on right border - when isWall called - then returns false`() {
        // given
        val spaceship = createSpaceship(Direction.RIGHT, x = 5, y = 5)
        val courseChecker = CourseChecker(spaceship, emptyList(), ScreenSize(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertFalse(result)
    }

    @Test
    fun `given spaceship moving down on bottom border - when isWall called - then returns true`() {
        // given
        val spaceship = createSpaceship(Direction.DOWN, x = 5, y = 10)
        val courseChecker = CourseChecker(spaceship, emptyList(), ScreenSize(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertTrue(result)
    }

    @Test
    fun `given spaceship moving down not on bottom border - when isWall called - then returns false`() {
        // given
        val spaceship = createSpaceship(Direction.DOWN, x = 5, y = 5)
        val courseChecker = CourseChecker(spaceship, emptyList(), ScreenSize(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertFalse(result)
    }

    @Test
    fun `given spaceship moving left on left border - when isWall called - then returns true`() {
        // given
        val spaceship = createSpaceship(Direction.LEFT, x = 1, y = 5)
        val courseChecker = CourseChecker(spaceship, emptyList(), ScreenSize(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertTrue(result)
    }

    @Test
    fun `given spaceship moving left not on left border - when isWall called - then returns false`() {
        // given
        val spaceship = createSpaceship(Direction.LEFT, x = 5, y = 5)
        val courseChecker = CourseChecker(spaceship, emptyList(), ScreenSize(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertFalse(result)
    }

    private fun createSpaceship(
        direction: Direction,
        x: Int = 5,
        y: Int = 5,
    ): Spaceship {
        val position = Position(x, y)
        val userId = UserId(1)
        val spaceship = Spaceship(position, userId)
        spaceship.direction = direction
        return spaceship
    }

    private fun adjacentCoordinates(direction: Direction): Position {
        return when (direction) {
            Direction.UP -> Position(5, 4)
            Direction.RIGHT -> Position(6, 5)
            Direction.DOWN -> Position(5, 6)
            Direction.LEFT -> Position(4, 5)
        }
    }
}
