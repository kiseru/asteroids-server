package com.kiseru.asteroids.server.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GarbageTest {

    @Test
    fun `test type`() {
        // given
        val garbage = Garbage(1, 1)

        // when
        val actual = garbage.type

        // then
        assertEquals(Type.GARBAGE, actual)
    }

    @Test
    fun `test view`() {
        // given
        val garbage = Garbage(1, 1)

        // when
        val actual = garbage.view()

        // then
        assertEquals("G", actual)
    }
}
