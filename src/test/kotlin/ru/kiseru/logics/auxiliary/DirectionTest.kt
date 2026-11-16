package ru.kiseru.logics.auxiliary

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertEquals

class DirectionTest {

    @ParameterizedTest
    @CsvSource(
        "UP,DOWN",
        "DOWN,UP",
        "LEFT,RIGHT",
        "RIGHT,LEFT"
    )
    fun `given direction - when called opposite - then return opposite direction`(
        initial: Direction,
        expected: Direction,
    ) {
        // when
        val actual = initial.opposite()

        // then
        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @EnumSource
    fun `given direction - when called opposite twice - then return same direction`(initial: Direction) {
        // when
        val actual = initial.opposite()
            .opposite()

        // then
        assertEquals(initial, actual)
    }
}
