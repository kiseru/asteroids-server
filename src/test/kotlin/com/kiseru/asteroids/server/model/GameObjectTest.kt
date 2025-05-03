package com.kiseru.asteroids.server.model

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class GameObjectTest {

    @Test
    fun `test equals when other is the same`() {
        // given
        val gameObject = Garbage(1, 1)

        // when
        val actual = gameObject.equals(gameObject)

        // then
        assertTrue(actual)
    }

    @Test
    fun `test equals when other is null`() {
        // given
        val gameObject = Garbage(1, 1)

        // when
        val actual = gameObject.equals(null)

        // then
        assertFalse(actual)
    }

    @Test
    fun `test equals when other instance's class is different`() {
        // given
        val gameObject = Garbage(1, 1)

        // when
        val actual = gameObject.equals("Some cool string")

        // then
        assertFalse(actual)
    }

    @ParameterizedTest
    @CsvSource(
        "2,2,false",
        "1,2,false",
        "1,1,true",
    )
    fun `test equals`(x: Int, y: Int, expected: Boolean) {
        // given
        val gameObject = Garbage(1, 1)
        val other = Asteroid(x, y)

        // when
        val actual = gameObject.equals(other)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `test hashCode`() {
        // given
        val gameObject = Garbage(1, 1)
        val other = Asteroid(1, 1)

        // when
        val actual = gameObject.hashCode() == other.hashCode()

        // then
        assertTrue(actual)
    }
}