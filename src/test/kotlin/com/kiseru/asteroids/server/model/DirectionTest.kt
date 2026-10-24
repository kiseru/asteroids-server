package com.kiseru.asteroids.server.model

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertEquals

class DirectionTest {

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test opposite`(direction: Direction) {
        // when
        val actual = direction.opposite()

        // then
        val expected = when (direction) {
            Direction.UP -> Direction.DOWN
            Direction.DOWN -> Direction.UP
            Direction.LEFT -> Direction.RIGHT
            Direction.RIGHT -> Direction.LEFT
        }
        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test opposite of opposite returns the same direction`(direction: Direction) {
        // when
        val actual = direction.opposite().opposite()

        // then
        assertEquals(direction, actual)
    }
}
