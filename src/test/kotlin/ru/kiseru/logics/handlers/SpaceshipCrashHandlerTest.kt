package ru.kiseru.logics.handlers

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.kiseru.logics.Game
import ru.kiseru.logics.Screen
import ru.kiseru.logics.auxiliary.Coordinates
import ru.kiseru.logics.auxiliary.Type
import ru.kiseru.logics.models.Asteroid
import ru.kiseru.logics.models.Garbage
import ru.kiseru.logics.models.Point
import ru.kiseru.logics.models.Spaceship

class SpaceshipCrashHandlerTest {

    @Test
    fun `given garbage on spaceship coordinates - when check called - then spaceship and garbage crashed`() {
        // given
        val coordinates = Coordinates(5, 5)
        val spaceship = mockSpaceship(coordinates)
        val garbage = Garbage(coordinates)
        val game = mockGame(spaceship, garbage)
        val handler = SpaceshipCrashHandler(game, spaceship)

        // when
        handler.check()

        // then
        verify(spaceship).crash(Type.GARBAGE)
        assertFalse(garbage.isVisible())
    }

    @Test
    fun `given asteroid on spaceship coordinates - when check called - then spaceship and asteroid crashed`() {
        // given
        val coordinates = Coordinates(5, 5)
        val spaceship = mockSpaceship(coordinates)
        val asteroid = Asteroid(coordinates)
        val game = mockGame(spaceship, asteroid)
        val handler = SpaceshipCrashHandler(game, spaceship)

        // when
        handler.check()

        // then
        verify(spaceship).crash(Type.ASTEROID)
        assertFalse(asteroid.isVisible())
    }

    @Test
    fun `given another spaceship on same coordinates - when check called - then no one crashed`() {
        // given
        val coordinates = Coordinates(5, 5)
        val spaceship = mockSpaceship(coordinates)
        val anotherSpaceship = mockPoint(Type.SPACESHIP, coordinates, isVisible = true)
        val game = mockGame(spaceship, anotherSpaceship)
        val handler = SpaceshipCrashHandler(game, spaceship)

        // when
        handler.check()

        // then
        verify(spaceship, never()).crash(any())
    }

    @Test
    fun `given invisible point on spaceship coordinates - when check called - then spaceship not crashed`() {
        // given
        val coordinates = Coordinates(5, 5)
        val spaceship = mockSpaceship(coordinates)
        val invisibleGarbage = Garbage(coordinates)
        invisibleGarbage.crash()
        val game = mockGame(spaceship, invisibleGarbage)
        val handler = SpaceshipCrashHandler(game, spaceship)

        // when
        handler.check()

        // then
        verify(spaceship, never()).crash(any())
        assertFalse(invisibleGarbage.isVisible())
    }

    @Test
    fun `given spaceship inside screen and no other points - when check called - then spaceship not crashed`() {
        // given
        val coordinates = Coordinates(5, 5)
        val spaceship = mockSpaceship(coordinates)
        val game = mockGame(spaceship)
        val handler = SpaceshipCrashHandler(game, spaceship)

        // when
        handler.check()

        // then
        verify(spaceship, never()).crash(any())
    }

    @ParameterizedTest
    @CsvSource(
        "0, 5",
        "5, 0",
        "11, 5",
        "5, 11",
    )
    fun `given spaceship out of screen bounds - when check called - then spaceship crashed into wall`(
        x: Int,
        y: Int,
    ) {
        // given
        val spaceship = mockSpaceship(Coordinates(x, y))
        val game = mockGame(spaceship, screen = Screen(10, 10))
        val handler = SpaceshipCrashHandler(game, spaceship)

        // when
        handler.check()

        // then
        verify(spaceship).crash(Type.WALL)
    }

    private fun mockSpaceship(coordinates: Coordinates): Spaceship {
        val spaceship = mock<Spaceship>()
        whenever(spaceship.coordinates).thenReturn(coordinates)
        whenever(spaceship.x).thenReturn(coordinates.x)
        whenever(spaceship.y).thenReturn(coordinates.y)
        return spaceship
    }

    private fun mockPoint(type: Type, coordinates: Coordinates, isVisible: Boolean): Point {
        val point = mock<Point>()
        whenever(point.type).thenReturn(type)
        whenever(point.coordinates).thenReturn(coordinates)
        whenever(point.isVisible()).thenReturn(isVisible)
        return point
    }

    private fun mockGame(vararg points: Point, screen: Screen = Screen(10, 10)): Game {
        val game = mock<Game>()
        whenever(game.pointsOnScreen).thenReturn(points.toList())
        whenever(game.screen).thenReturn(screen)
        return game
    }
}
