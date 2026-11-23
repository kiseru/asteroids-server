package ru.kiseru.logics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ScoreTest {

    @Test
    fun `given new score - when created - then value is zero`() {
        // when
        val score = Score()

        // then
        assertEquals(0, score.value())
    }

    @Test
    fun `given score - when increaseBy called - then new score with increased value returned`() {
        // given
        val score = Score()

        // when
        val actual = score.increaseBy(10)

        // then
        assertEquals(10, actual.value())
        assertEquals(0, score.value())
    }

    @Test
    fun `given score - when decreaseBy called - then new score with decreased value returned`() {
        // given
        val score = Score(100)

        // when
        val actual = score.decreaseBy(50)

        // then
        assertEquals(50, actual.value())
        assertEquals(100, score.value())
    }

    @Test
    fun `given score with negative value - when decreaseBy called - then exception thrown`() {
        // given
        val score = Score(-10)

        // when
        val exception = assertThrows<IllegalStateException> { score.decreaseBy(50) }

        // then
        assertEquals("cannot change score value if value < 0", exception.message)
    }
}
