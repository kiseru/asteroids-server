package com.kiseru.asteroids.server.model

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertEquals

class PositionTest {

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test aheadOf`(direction: Direction) {
        // given
        val position = Position(2, 2)

        // when
        val actual = position.aheadOf(direction)

        // then
        val expected = when (direction) {
            Direction.UP -> Position(2, 1)
            Direction.DOWN -> Position(2, 3)
            Direction.LEFT -> Position(1, 2)
            Direction.RIGHT -> Position(3, 2)
        }
        assertEquals(expected, actual)
    }
}
