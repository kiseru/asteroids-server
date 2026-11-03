package ru.kiseru.logics.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kiseru.logics.auxiliary.Coordinates
import ru.kiseru.logics.auxiliary.Type

class PointTest {

    private class TestPoint(coordinates: Coordinates) : Point(coordinates) {
        override fun getType(): Type = Type.SPACESHIP
    }

    @Test
    fun `given coordinates - when point created - then it is visible and stores coordinates`() {
        // given
        val coordinates = Coordinates(3, 7)

        // when
        val point = TestPoint(coordinates)

        // then
        assertEquals(coordinates, point.coordinates)
        assertTrue(point.isVisible)
    }

    @Test
    fun `given point - when getX and getY called - then values from coordinates returned`() {
        // given
        val point = TestPoint(Coordinates(1, 5))

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
        val coordinates = Coordinates(2, 9)
        val point = TestPoint(coordinates)

        // when
        val actual = point.coordinates

        // then
        assertEquals(coordinates, actual)
    }

    @Test
    fun `given point - when isVisible called - then true returned`() {
        // given
        val point = TestPoint(Coordinates(0, 0))

        // when & then
        assertTrue(point.isVisible)
    }

    @Test
    fun `given point - when getType called - then type of subclass returned`() {
        // given
        val point = TestPoint(Coordinates(0, 0))

        // when
        val type = point.type

        // then
        assertEquals(Type.SPACESHIP, type)
    }
}
