package com.kiseru.asteroids.server.model

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
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
}