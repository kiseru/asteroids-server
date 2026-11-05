package ru.kiseru.logics

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.kiseru.logics.auxiliary.Coordinates
import ru.kiseru.logics.auxiliary.Direction
import ru.kiseru.logics.auxiliary.Type
import ru.kiseru.logics.models.Point
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
        val spaceship = mockSpaceship(direction)
        val asteroidCoordinates = adjacentCoordinates(direction)
        val points = listOf(mockPoint(Type.ASTEROID, asteroidCoordinates))
        val courseChecker = CourseChecker(spaceship, points, Screen(10, 10))

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
        val spaceship = mockSpaceship(direction)
        val points = listOf(mockPoint(Type.ASTEROID, Coordinates(1, 1)))
        val courseChecker = CourseChecker(spaceship, points, Screen(10, 10))

        // when
        val result = courseChecker.isAsteroid()

        // then
        assertFalse(result)
    }

    @Test
    fun `given garbage on adjacent cell in spaceship direction - when isAsteroid called - then returns false`() {
        // given
        val spaceship = mockSpaceship(Direction.UP)
        val points = listOf(mockPoint(Type.GARBAGE, Coordinates(5, 4)))
        val courseChecker = CourseChecker(spaceship, points, Screen(10, 10))

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
        val spaceship = mockSpaceship(direction)
        val garbageCoordinates = adjacentCoordinates(direction)
        val points = listOf(mockPoint(Type.GARBAGE, garbageCoordinates))
        val courseChecker = CourseChecker(spaceship, points, Screen(10, 10))

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
        val spaceship = mockSpaceship(direction)
        val points = listOf(mockPoint(Type.GARBAGE, Coordinates(1, 1)))
        val courseChecker = CourseChecker(spaceship, points, Screen(10, 10))

        // when
        val result = courseChecker.isGarbage()

        // then
        assertFalse(result)
    }

    @Test
    fun `given asteroid on adjacent cell in spaceship direction - when isGarbage called - then returns false`() {
        // given
        val spaceship = mockSpaceship(Direction.UP)
        val points = listOf(mockPoint(Type.ASTEROID, Coordinates(5, 4)))
        val courseChecker = CourseChecker(spaceship, points, Screen(10, 10))

        // when
        val result = courseChecker.isGarbage()

        // then
        assertFalse(result)
    }

    @Test
    fun `given spaceship moving up on top border - when isWall called - then returns true`() {
        // given
        val spaceship = mockSpaceship(Direction.UP, x = 5, y = 1)
        val courseChecker = CourseChecker(spaceship, emptyList(), Screen(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertTrue(result)
    }

    @Test
    fun `given spaceship moving up not on top border - when isWall called - then returns false`() {
        // given
        val spaceship = mockSpaceship(Direction.UP, x = 5, y = 5)
        val courseChecker = CourseChecker(spaceship, emptyList(), Screen(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertFalse(result)
    }

    @Test
    fun `given spaceship moving right on right border - when isWall called - then returns true`() {
        // given
        val spaceship = mockSpaceship(Direction.RIGHT, x = 10, y = 5)
        val courseChecker = CourseChecker(spaceship, emptyList(), Screen(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertTrue(result)
    }

    @Test
    fun `given spaceship moving right not on right border - when isWall called - then returns false`() {
        // given
        val spaceship = mockSpaceship(Direction.RIGHT, x = 5, y = 5)
        val courseChecker = CourseChecker(spaceship, emptyList(), Screen(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertFalse(result)
    }

    @Test
    fun `given spaceship moving down on bottom border - when isWall called - then returns true`() {
        // given
        val spaceship = mockSpaceship(Direction.DOWN, x = 5, y = 10)
        val courseChecker = CourseChecker(spaceship, emptyList(), Screen(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertTrue(result)
    }

    @Test
    fun `given spaceship moving down not on bottom border - when isWall called - then returns false`() {
        // given
        val spaceship = mockSpaceship(Direction.DOWN, x = 5, y = 5)
        val courseChecker = CourseChecker(spaceship, emptyList(), Screen(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertFalse(result)
    }

    @Test
    fun `given spaceship moving left on left border - when isWall called - then returns true`() {
        // given
        val spaceship = mockSpaceship(Direction.LEFT, x = 1, y = 5)
        val courseChecker = CourseChecker(spaceship, emptyList(), Screen(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertTrue(result)
    }

    @Test
    fun `given spaceship moving left not on left border - when isWall called - then returns false`() {
        // given
        val spaceship = mockSpaceship(Direction.LEFT, x = 5, y = 5)
        val courseChecker = CourseChecker(spaceship, emptyList(), Screen(10, 10))

        // when
        val result = courseChecker.isWall()

        // then
        assertFalse(result)
    }

    private fun mockSpaceship(
        direction: Direction,
        x: Int = 5,
        y: Int = 5,
    ): Spaceship {
        val spaceship = mock<Spaceship>()
        whenever(spaceship.direction).thenReturn(direction)
        whenever(spaceship.x).thenReturn(x)
        whenever(spaceship.y).thenReturn(y)
        return spaceship
    }

    private fun mockPoint(type: Type, coordinates: Coordinates): Point {
        val point = mock<Point>()
        whenever(point.type).thenReturn(type)
        whenever(point.coordinates).thenReturn(coordinates)
        return point
    }

    private fun adjacentCoordinates(direction: Direction): Coordinates {
        return when (direction) {
            Direction.UP -> Coordinates(5, 4)
            Direction.RIGHT -> Coordinates(6, 5)
            Direction.DOWN -> Coordinates(5, 6)
            Direction.LEFT -> Coordinates(4, 5)
        }
    }
}
