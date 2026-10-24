package com.kiseru.asteroids.server.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ScoreTest {

    @Test
    fun `test increaseBy when points are positive`() {
        // given
        val score = Score(100)

        // when
        val actual = score.increaseBy(1)

        // then
        assertEquals(Score(101), actual)
    }

    @Test
    fun `test increaseBy when points are not positive`() {
        // given
        val score = Score(100)

        // when & then
        assertThrows<IllegalArgumentException> { score.increaseBy(0) }
    }

    @Test
    fun `test decreaseBy when points are positive`() {
        // given
        val score = Score(100)

        // when
        val actual = score.decreaseBy(1)

        // then
        assertEquals(Score(99), actual)
    }

    @Test
    fun `test decreaseBy when points are not positive`() {
        // given
        val score = Score(100)

        // when & then
        assertThrows<IllegalArgumentException> { score.decreaseBy(0) }
    }
}