package ru.kiseru.logics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class StepsTest {

    @Test
    fun `given new steps - when created - then value is zero`() {
        // when
        val steps = Steps()

        // then
        assertEquals(0, steps.value)
    }

    @Test
    fun `given steps - when increment called - then new steps with increased value returned`() {
        // given
        val steps = Steps()

        // when
        val actual = steps.increment()

        // then
        assertEquals(1, actual.value)
        assertEquals(0, steps.value)
    }

    @Test
    fun `given steps with max value - when increment called - then exception thrown`() {
        // given
        var steps = Steps()
        repeat(1500) {
            steps = steps.increment()
        }

        // when & then
        assertThrows<IllegalStateException> { steps.increment() }
    }
}
