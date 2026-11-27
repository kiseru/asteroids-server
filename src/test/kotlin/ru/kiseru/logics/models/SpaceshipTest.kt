package ru.kiseru.logics.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.kiseru.logics.Score
import ru.kiseru.logics.UserId
import ru.kiseru.logics.auxiliary.Direction
import ru.kiseru.logics.auxiliary.Position
import ru.kiseru.logics.auxiliary.Type

class SpaceshipTest {

    @Test
    fun `given coordinates and userId - when spaceship created - then it is visible and stores coordinates`() {
        // given
        val position = Position(3, 7)
        val userId = UserId(1)

        // when
        val spaceship = Spaceship(position, userId)

        // then
        assertEquals(position, spaceship.position)
        assertTrue(spaceship.isVisible)
    }

    @Test
    fun `given coordinates and userId - when spaceship created - then score is zero`() {
        // given
        val userId = UserId(1)

        // when
        val spaceship = Spaceship(Position(3, 7), userId)

        // then
        assertEquals(0, spaceship.score.value())
    }

    @Test
    fun `given spaceship - when getType called - then SPACESHIP type returned`() {
        // given
        val userId = UserId(1)
        val spaceship = Spaceship(Position(1, 2), userId)

        // when
        val actual = spaceship.type

        // then
        assertEquals(Type.SPACESHIP, actual)
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
        val userId = UserId(1)
        val spaceship = Spaceship(Position(initialX, initialY), userId)
        spaceship.direction = direction

        // when
        spaceship.go()

        // then
        assertEquals(Position(expectedX, expectedY), spaceship.position)
    }

    @Test
    fun `given alive spaceship - when addScore called - then score increased by 10`() {
        // given
        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)

        // when
        spaceship.addScore()

        // then
        assertEquals(10, spaceship.score.value())
    }

    @Test
    fun `given destroyed spaceship - when addScore called - then illegal state exception thrown`() {
        // given
        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)
        spaceship.destroy()

        // when & then
        assertThrows<IllegalStateException> { spaceship.addScore() }
    }

    @Test
    fun `given destroyed spaceship - when subtractScore called - then illegal state exception thrown`() {
        // given
        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)
        spaceship.destroy()

        // when & then
        assertThrows<IllegalStateException> { spaceship.subtractScore() }
    }

    @Test
    fun `given alive spaceship with score - when subtractScore called - then score decreased by 50 and spaceship stays alive`() {
        // given
        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)
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
        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)
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
        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)
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
        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)

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
        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)
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
        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)

        // when
        val actual = spaceship.status

        // then
        assertEquals(Spaceship.Status.ALIVE, actual)
    }

    @Test
    fun `given alive spaceship - when destroy called - then spaceship is destroyed and invisible`() {
        // given
        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)

        // when
        spaceship.destroy()

        // then
        assertEquals(Spaceship.Status.DESTROYED, spaceship.status)
        assertFalse(spaceship.isVisible)
    }

    @Test
    fun `given destroyed spaceship - when destroy called - then illegal state exception thrown`() {
        // given
        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)
        spaceship.destroy()

        // when & then
        assertThrows<IllegalStateException> { spaceship.destroy() }
    }

    @Test
    fun `given spaceship - when setDirection called - then direction stored`() {
        // given
        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)

        // when
        spaceship.direction = Direction.LEFT

        // then
        assertEquals(Direction.LEFT, spaceship.direction)
    }

    @ParameterizedTest
    @CsvSource(
        "UP,5,4",
        "DOWN,5,6",
        "LEFT,4,5",
        "RIGHT,6,5",
    )
    fun `given spaceship - when positionAhead called - then returns position ahead of spaceship`(
        direction: Direction,
        expectedX: Int,
        expectedY: Int,
    ) {
        // given
        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)
        spaceship.direction = direction

        // when
        val actual = spaceship.positionAhead()

        // then
        assertEquals(Position(expectedX, expectedY), actual)
    }
}
