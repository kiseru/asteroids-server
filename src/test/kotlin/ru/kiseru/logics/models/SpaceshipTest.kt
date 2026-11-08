package ru.kiseru.logics.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.kiseru.logics.Game
import ru.kiseru.logics.Screen
import ru.kiseru.logics.auxiliary.Coordinates
import ru.kiseru.logics.auxiliary.Direction
import ru.kiseru.logics.auxiliary.Type
import ru.kiseru.logics.CourseChecker
import ru.kiseru.server.User
import ru.kiseru.server.room.Room

class SpaceshipTest {

    @Test
    fun `given coordinates and user - when spaceship created - then it is visible and stores coordinates`() {
        // given
        val coordinates = Coordinates(3, 7)
        val owner = mockUser(mockRoom(mock<Game>()))

        // when
        val spaceship = Spaceship(coordinates, owner)

        // then
        assertEquals(coordinates, spaceship.coordinates)
        assertTrue(spaceship.isVisible)
    }

    @Test
    fun `given spaceship - when getType called - then SPACESHIP type returned`() {
        // given
        val spaceship = Spaceship(Coordinates(1, 2), mockUser(mockRoom(mock<Game>())))

        // when
        val actual = spaceship.type

        // then
        assertEquals(Type.SPACESHIP, actual)
    }

    @Test
    fun `given visible spaceship - when render called - then owner id drawn on screen`() {
        // given
        val screen = Screen(5, 5)
        val owner = mockUser(mockRoom(mock<Game>()))
        val spaceship = Spaceship(Coordinates(2, 3), owner)

        // when
        spaceship.render(screen)

        // then
        val lines = screen.display().split("\n")
        assertEquals("1", lines[2].split("\t")[1])
    }

    @Test
    fun `given invisible spaceship - when render called - then nothing drawn on screen`() {
        // given
        val screen = Screen(5, 5)
        val spaceship = Spaceship(Coordinates(2, 3), mockUser(mockRoom(mock<Game>())))
        spaceship.isVisible = false

        // when
        spaceship.render(screen)

        // then
        val lines = screen.display().split("\n")
        assertEquals(".", lines[2].split("\t")[1])
    }

    @ParameterizedTest
    @CsvSource(
        "UP, 5, 5, 5, 4",
        "RIGHT, 5, 5, 6, 5",
        "DOWN, 5, 5, 5, 6",
        "LEFT, 5, 5, 4, 5",
    )
    fun `given spaceship with direction - when go called - then coordinates changed by direction`(
        direction: Direction,
        initialX: Int,
        initialY: Int,
        expectedX: Int,
        expectedY: Int,
    ) {
        // given
        val spaceship = Spaceship(Coordinates(initialX, initialY), mockUser(mockRoom(mock<Game>())))
        spaceship.direction = direction

        // when
        spaceship.go()

        // then
        assertEquals(Coordinates(expectedX, expectedY), spaceship.coordinates)
    }

    @Test
    fun `given spaceship crashed into asteroid - when crash called - then owner score subtracted`() {
        // given
        val game = mock<Game>()
        val owner = mockUser(mockRoom(game))
        val spaceship = Spaceship(Coordinates(5, 5), owner)

        // when
        spaceship.crash(Type.ASTEROID)

        // then
        verify(owner).subtractScore()
    }

    @Test
    fun `given spaceship crashed into garbage - when crash called - then owner score added and game counter incremented`() {
        // given
        val game = mock<Game>()
        whenever(game.incrementCollectedGarbageCount()).thenReturn(1)
        whenever(game.garbageNumber).thenReturn(10)
        val owner = mockUser(mockRoom(game))
        val spaceship = Spaceship(Coordinates(5, 5), owner)

        // when
        spaceship.crash(Type.GARBAGE)

        // then
        verify(owner).addScore()
        verify(game).incrementCollectedGarbageCount()
    }

    @Test
    fun `given spaceship collected last garbage - when crash called - then owner score added and counter incremented`() {
        // given
        val game = mock<Game>()
        whenever(game.incrementCollectedGarbageCount()).thenReturn(5)
        whenever(game.garbageNumber).thenReturn(5)
        val owner = mockUser(mockRoom(game))
        val spaceship = Spaceship(Coordinates(5, 5), owner)

        // when
        spaceship.crash(Type.GARBAGE)

        // then
        verify(owner).addScore()
        verify(game).incrementCollectedGarbageCount()
    }

    @Test
    fun `given spaceship crashed into wall - when crash called - then coordinates rolled back and owner score subtracted`() {
        // given
        val game = mock<Game>()
        val owner = mockUser(mockRoom(game))
        val spaceship = Spaceship(Coordinates(5, 5), owner)
        spaceship.direction = Direction.UP
        spaceship.go()

        // when
        spaceship.crash(Type.WALL)

        // then
        assertEquals(Coordinates(5, 5), spaceship.coordinates)
        verify(owner).subtractScore()
    }

    @Test
    fun `given spaceship with dead owner - when crash called - then spaceship becomes invisible`() {
        // given
        val game = mock<Game>()
        whenever(game.incrementCollectedGarbageCount()).thenReturn(1)
        whenever(game.garbageNumber).thenReturn(10)
        val owner = mockUser(mockRoom(game))
        whenever(owner.isAlive()).thenReturn(false)
        val spaceship = Spaceship(Coordinates(5, 5), owner)
        assertTrue(spaceship.isVisible)

        // when
        spaceship.crash(Type.GARBAGE)

        // then
        assertFalse(spaceship.isVisible)
    }

    @Test
    fun `given spaceship - when setDirection called - then direction stored`() {
        // given
        val spaceship = Spaceship(Coordinates(5, 5), mockUser(mockRoom(mock<Game>())))

        // when
        spaceship.direction = Direction.LEFT

        // then
        assertEquals(Direction.LEFT, spaceship.direction)
    }

    @Test
    fun `given spaceship - when setCourseChecker called - then course checker stored`() {
        // given
        val spaceship = Spaceship(Coordinates(5, 5), mockUser(mockRoom(mock<Game>())))
        val courseChecker = CourseChecker(spaceship, emptyList(), Screen(5, 5))

        // when
        spaceship.courseChecker = courseChecker

        // then
        assertSame(courseChecker, spaceship.courseChecker)
    }

    @Test
    fun `given spaceship with alive owner - when isOwnerAlive called - then owner liveness returned`() {
        // given
        val owner = mockUser(mockRoom(mock<Game>()))
        val spaceship = Spaceship(Coordinates(5, 5), owner)

        // when
        val actual = spaceship.isOwnerAlive

        // then
        assertTrue(actual)
    }

    @Test
    fun `given spaceship with dead owner - when isOwnerAlive called - then false returned`() {
        // given
        val owner = mockUser(mockRoom(mock<Game>()))
        whenever(owner.isAlive()).thenReturn(false)
        val spaceship = Spaceship(Coordinates(5, 5), owner)

        // when & then
        assertFalse(spaceship.isOwnerAlive)
    }

    private fun mockUser(room: Room): User {
        val user = mock<User>()
        whenever(user.room).thenReturn(room)
        whenever(user.isAlive()).thenReturn(true)
        whenever(user.id).thenReturn(1L)
        return user
    }

    private fun mockRoom(game: Game): Room {
        val room = mock<Room>()
        whenever(room.game).thenReturn(game)
        return room
    }
}
