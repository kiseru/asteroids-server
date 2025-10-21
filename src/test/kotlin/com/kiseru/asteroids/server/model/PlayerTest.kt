package com.kiseru.asteroids.server.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlayerTest {

    @Test
    fun `test default values after creation`() {
        // given
        val player = Player()

        // when & then
        assertEquals(Direction.UP, player.direction)
        assertEquals(0, player.steps)
        assertEquals(100, player.score)
        assertTrue(player.isAlive)
    }
}