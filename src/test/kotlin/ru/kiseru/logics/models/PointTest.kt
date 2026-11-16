package ru.kiseru.logics.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kiseru.logics.auxiliary.Position
import ru.kiseru.logics.auxiliary.Type

class PointTest {

    private class TestPoint(position: Position) : Point(position) {
        override fun getType(): Type = Type.SPACESHIP
    }

    @Test
    fun `given coordinates - when point created - then it is visible and stores coordinates`() {
        // given
        val position = Position(3, 7)

        // when
        val point = TestPoint(position)

        // then
        assertEquals(position, point.position)
        assertTrue(point.isVisible)
    }

    @Test
    fun `given point - when getX and getY called - then values from coordinates returned`() {
        // given
        val point = TestPoint(Position(1, 5))

        // when
        val actualX = point.x
        val actualY = point.y

        // then
        assertEquals(1, actualX)
        assertEquals(5, actualY)
    }

    @Test
    fun `given point - when getCoordinates called - then stored coordinates returned`() {
        // given
        val position = Position(2, 9)
        val point = TestPoint(position)

        // when
        val actual = point.position

        // then
        assertEquals(position, actual)
    }

    @Test
    fun `given point - when isVisible called - then true returned`() {
        // given
        val point = TestPoint(Position(0, 0))

        // when & then
        assertTrue(point.isVisible)
    }

    @Test
    fun `given point - when getType called - then type of subclass returned`() {
        // given
        val point = TestPoint(Position(0, 0))

        // when
        val type = point.type

        // then
        assertEquals(Type.SPACESHIP, type)
    }
}
