package com.kiseru.asteroids.server.model

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

    @Test
    fun `test isOutOfBounds when inside the field`() {
        // given
        val position = Position(2, 2)

        // when
        val actual = position.isOutOfBounds(3, 3)

        // then
        assertFalse(actual)
    }

    @Test
    fun `test isOutOfBounds when x is less than 1`() {
        // given
        val position = Position(0, 2)

        // when
        val actual = position.isOutOfBounds(3, 3)

        // then
        assertTrue(actual)
    }

    @Test
    fun `test isOutOfBounds when y is less than 1`() {
        // given
        val position = Position(2, 0)

        // when
        val actual = position.isOutOfBounds(3, 3)

        // then
        assertTrue(actual)
    }

    @Test
    fun `test isOutOfBounds when x is greater than width`() {
        // given
        val position = Position(4, 2)

        // when
        val actual = position.isOutOfBounds(3, 3)

        // then
        assertTrue(actual)
    }

    @Test
    fun `test isOutOfBounds when y is greater than height`() {
        // given
        val position = Position(2, 4)

        // when
        val actual = position.isOutOfBounds(3, 3)

        // then
        assertTrue(actual)
    }
}
