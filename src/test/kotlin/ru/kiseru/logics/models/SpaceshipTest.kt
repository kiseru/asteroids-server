package ru.kiseru.logics.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.kiseru.logics.Game
import ru.kiseru.logics.Screen
import ru.kiseru.logics.User
import ru.kiseru.logics.auxiliary.Position
import ru.kiseru.logics.auxiliary.ScreenSize
import ru.kiseru.logics.auxiliary.Direction
import ru.kiseru.logics.auxiliary.Type
import ru.kiseru.logics.CourseChecker
import ru.kiseru.logics.Score
import ru.kiseru.server.UserHandler
import ru.kiseru.server.room.Room

class SpaceshipTest {

    @Test
    fun `given coordinates and userHandler - when spaceship created - then it is visible and stores coordinates`() {
        // given
        val position = Position(3, 7)
        val owner = mockUserHandler(mockRoom(mock<Game>()))

        // when
        val spaceship = Spaceship(position, owner)

        // then
        assertEquals(position, spaceship.position)
        assertTrue(spaceship.isVisible)
    }

    @Test
    fun `given coordinates and userHandler - when spaceship created - then score is zero`() {
        // given
        val owner = mockUserHandler(mockRoom(mock<Game>()))

        // when
        val spaceship = Spaceship(Position(3, 7), owner)

        // then
        assertEquals(0, spaceship.score.value())
    }

    @Test
    fun `given spaceship - when getType called - then SPACESHIP type returned`() {
        // given
        val spaceship = Spaceship(Position(1, 2), mockUserHandler(mockRoom(mock<Game>())))

        // when
        val actual = spaceship.type

        // then
        assertEquals(Type.SPACESHIP, actual)
    }

    @Test
    fun `given visible spaceship - when render called - then owner id drawn on screen`() {
        // given
        val screen = Screen(ScreenSize(5, 5))
        val owner = mockUserHandler(mockRoom(mock<Game>()))
        val spaceship = Spaceship(Position(2, 3), owner)

        // when
        spaceship.render(screen)

        // then
        val lines = screen.display().split("\n")
        assertEquals("1", lines[2].split("\t")[1])
    }

    @Test
    fun `given invisible spaceship - when render called - then nothing drawn on screen`() {
        // given
        val screen = Screen(ScreenSize(5, 5))
        val spaceship = Spaceship(Position(2, 3), mockUserHandler(mockRoom(mock<Game>())))
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
        val spaceship = Spaceship(Position(initialX, initialY), mockUserHandler(mockRoom(mock<Game>())))
        spaceship.direction = direction

        // when
        spaceship.go()

        // then
        assertEquals(Position(expectedX, expectedY), spaceship.position)
    }

    @Test
    fun `given spaceship with score - when crash into asteroid - then score decreased by 50`() {
        // given
        val game = mock<Game>()
        val owner = mockUserHandler(mockRoom(game))
        val spaceship = Spaceship(Position(5, 5), owner)
        spaceship.score = Score(100)

        // when
        spaceship.crash(Type.ASTEROID)

        // then
        assertEquals(50, spaceship.score.value())
    }

    @Test
    fun `given spaceship with zero score - when crash into asteroid - then spaceship destroyed`() {
        // given
        val game = mock<Game>()
        val owner = mockUserHandler(mockRoom(game))
        val spaceship = Spaceship(Position(5, 5), owner)

        // when
        spaceship.crash(Type.ASTEROID)

        // then
        assertEquals(-50, spaceship.score.value())
        assertEquals(Spaceship.Status.DESTROYED, spaceship.status)
        assertFalse(spaceship.isVisible)
    }

    @Test
    fun `given spaceship crashed into garbage - when crash called - then score increased by 10 and game counter incremented`() {
        // given
        val game = mock<Game>()
        whenever(game.incrementCollectedGarbageCount()).thenReturn(1)
        whenever(game.garbageNumber).thenReturn(10)
        val owner = mockUserHandler(mockRoom(game))
        val spaceship = Spaceship(Position(5, 5), owner)

        // when
        spaceship.crash(Type.GARBAGE)

        // then
        assertEquals(10, spaceship.score.value())
        verify(game).incrementCollectedGarbageCount()
    }

    @Test
    fun `given spaceship collected last garbage - when crash called - then score increased by 10 and counter incremented`() {
        // given
        val game = mock<Game>()
        whenever(game.incrementCollectedGarbageCount()).thenReturn(5)
        whenever(game.garbageNumber).thenReturn(5)
        val owner = mockUserHandler(mockRoom(game))
        val spaceship = Spaceship(Position(5, 5), owner)

        // when
        spaceship.crash(Type.GARBAGE)

        // then
        assertEquals(10, spaceship.score.value())
        verify(game).incrementCollectedGarbageCount()
    }

    @Test
    fun `given spaceship crashed into wall - when crash called - then coordinates rolled back and score decreased by 50`() {
        // given
        val game = mock<Game>()
        val owner = mockUserHandler(mockRoom(game))
        val spaceship = Spaceship(Position(5, 5), owner)
        spaceship.score = Score(100)
        spaceship.direction = Direction.UP
        spaceship.go()

        // when
        spaceship.crash(Type.WALL)

        // then
        assertEquals(Position(5, 5), spaceship.position)
        assertEquals(50, spaceship.score.value())
    }

    @Test
    fun `given alive spaceship - when addScore called - then score increased by 10`() {
        // given
        val spaceship = Spaceship(Position(5, 5), mockUserHandler(mockRoom(mock<Game>())))

        // when
        spaceship.addScore()

        // then
        assertEquals(10, spaceship.score.value())
    }

    @Test
    fun `given destroyed spaceship - when addScore called - then illegal state exception thrown`() {
        // given
        val spaceship = Spaceship(Position(5, 5), mockUserHandler(mockRoom(mock<Game>())))
        spaceship.destroy()

        // when & then
        assertThrows<IllegalStateException> { spaceship.addScore() }
    }

    @Test
    fun `given destroyed spaceship - when subtractScore called - then illegal state exception thrown`() {
        // given
        val spaceship = Spaceship(Position(5, 5), mockUserHandler(mockRoom(mock<Game>())))
        spaceship.destroy()

        // when & then
        assertThrows<IllegalStateException> { spaceship.subtractScore() }
    }

    @Test
    fun `given alive spaceship with score - when subtractScore called - then score decreased by 50 and spaceship stays alive`() {
        // given
        val spaceship = Spaceship(Position(5, 5), mockUserHandler(mockRoom(mock<Game>())))
        spaceship.score = Score(100)

        // when
        spaceship.subtractScore()

        // then
        assertEquals(50, spaceship.score.value())
        assertEquals(Spaceship.Status.ALIVE, spaceship.status)
        assertTrue(spaceship.isVisible)
    }

    @Test
    fun `given alive spaceship with score equal to penalty - when subtractScore called - then score is zero and spaceship stays alive`() {
        // given
        val spaceship = Spaceship(Position(5, 5), mockUserHandler(mockRoom(mock<Game>())))
        spaceship.score = Score(50)

        // when
        spaceship.subtractScore()

        // then
        assertEquals(0, spaceship.score.value())
        assertEquals(Spaceship.Status.ALIVE, spaceship.status)
        assertTrue(spaceship.isVisible)
    }

    @Test
    fun `given alive spaceship with score less than penalty - when subtractScore called - then score is negative and spaceship destroyed`() {
        // given
        val spaceship = Spaceship(Position(5, 5), mockUserHandler(mockRoom(mock<Game>())))
        spaceship.score = Score(30)

        // when
        spaceship.subtractScore()

        // then
        assertEquals(-20, spaceship.score.value())
        assertEquals(Spaceship.Status.DESTROYED, spaceship.status)
        assertFalse(spaceship.isVisible)
    }

    @Test
    fun `given alive spaceship with zero score - when subtractScore called - then score decreased by 50 and spaceship destroyed`() {
        // given
        val spaceship = Spaceship(Position(5, 5), mockUserHandler(mockRoom(mock<Game>())))

        // when
        spaceship.subtractScore()

        // then
        assertEquals(-50, spaceship.score.value())
        assertEquals(Spaceship.Status.DESTROYED, spaceship.status)
        assertFalse(spaceship.isVisible)
    }

    @Test
    fun `given alive spaceship with negative score - when subtractScore called - then score unchanged`() {
        // given
        val spaceship = Spaceship(Position(5, 5), mockUserHandler(mockRoom(mock<Game>())))
        spaceship.score = Score(-10)

        // when
        spaceship.subtractScore()

        // then
        assertEquals(-10, spaceship.score.value())
        assertEquals(Spaceship.Status.ALIVE, spaceship.status)
        assertTrue(spaceship.isVisible)
    }

    @Test
    fun `given new spaceship - when status requested - then alive status returned`() {
        // given
        val spaceship = Spaceship(Position(5, 5), mockUserHandler(mockRoom(mock<Game>())))

        // when
        val actual = spaceship.status

        // then
        assertEquals(Spaceship.Status.ALIVE, actual)
    }

    @Test
    fun `given alive spaceship - when destroy called - then spaceship is destroyed and invisible`() {
        // given
        val spaceship = Spaceship(Position(5, 5), mockUserHandler(mockRoom(mock<Game>())))

        // when
        spaceship.destroy()

        // then
        assertEquals(Spaceship.Status.DESTROYED, spaceship.status)
        assertFalse(spaceship.isVisible)
    }

    @Test
    fun `given destroyed spaceship - when destroy called - then illegal state exception thrown`() {
        // given
        val spaceship = Spaceship(Position(5, 5), mockUserHandler(mockRoom(mock<Game>())))
        spaceship.destroy()

        // when & then
        assertThrows<IllegalStateException> { spaceship.destroy() }
    }

    @Test
    fun `given spaceship - when setDirection called - then direction stored`() {
        // given
        val spaceship = Spaceship(Position(5, 5), mockUserHandler(mockRoom(mock<Game>())))

        // when
        spaceship.direction = Direction.LEFT

        // then
        assertEquals(Direction.LEFT, spaceship.direction)
    }

    @Test
    fun `given spaceship - when setCourseChecker called - then course checker stored`() {
        // given
        val spaceship = Spaceship(Position(5, 5), mockUserHandler(mockRoom(mock<Game>())))
        val courseChecker = CourseChecker(spaceship, emptyList(), Screen(ScreenSize(5, 5)))

        // when
        spaceship.courseChecker = courseChecker

        // then
        assertSame(courseChecker, spaceship.courseChecker)
    }

    private fun mockUserHandler(room: Room): UserHandler {
        val userHandler = mock<UserHandler>()
        whenever(userHandler.room).thenReturn(room)
        whenever(userHandler.user).thenReturn(User(1, "alice"))
        return userHandler
    }

    private fun mockRoom(game: Game): Room {
        val room = mock<Room>()
        whenever(room.game).thenReturn(game)
        return room
    }
}
