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
import ru.kiseru.logics.auxiliary.Position
import ru.kiseru.logics.auxiliary.ScreenSize
import ru.kiseru.logics.auxiliary.Type
import ru.kiseru.logics.models.Asteroid
import ru.kiseru.logics.models.Garbage
import ru.kiseru.logics.models.Point
import ru.kiseru.logics.models.Spaceship

class SpaceshipCrashHandlerTest {

    @Test
    fun `given garbage on spaceship coordinates - when check called - then spaceship and garbage crashed`() {
        // given
        val position = Position(5, 5)
        val spaceship = mockSpaceship(position)
        val garbage = Garbage(position)
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
        val position = Position(5, 5)
        val spaceship = mockSpaceship(position)
        val asteroid = Asteroid(position)
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
        val position = Position(5, 5)
        val spaceship = mockSpaceship(position)
        val anotherSpaceship = mockPoint(Type.SPACESHIP, position, isVisible = true)
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
        val position = Position(5, 5)
        val spaceship = mockSpaceship(position)
        val invisibleGarbage = Garbage(position)
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
        val position = Position(5, 5)
        val spaceship = mockSpaceship(position)
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
        val spaceship = mockSpaceship(Position(x, y))
        val game = mockGame(spaceship, screen = Screen(ScreenSize(10, 10)))
        val handler = SpaceshipCrashHandler(game, spaceship)

        // when
        handler.check()

        // then
        verify(spaceship).crash(Type.WALL)
    }

    private fun mockSpaceship(position: Position): Spaceship {
        val spaceship = mock<Spaceship>()
        whenever(spaceship.position).thenReturn(position)
        whenever(spaceship.x).thenReturn(position.x())
        whenever(spaceship.y).thenReturn(position.y())
        return spaceship
    }

    private fun mockPoint(type: Type, position: Position, isVisible: Boolean): Point {
        val point = mock<Point>()
        whenever(point.type).thenReturn(type)
        whenever(point.position).thenReturn(position)
        whenever(point.isVisible()).thenReturn(isVisible)
        return point
    }

    private fun mockGame(vararg points: Point, screen: Screen = Screen(ScreenSize(10, 10))): Game {
        val game = mock<Game>()
        whenever(game.pointsOnScreen).thenReturn(points.toList())
        whenever(game.screen).thenReturn(screen)
        return game
    }
}
