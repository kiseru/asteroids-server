package com.kiseru.asteroids.server.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class UserTest {

    @Test
    fun `test users with the same id are allowed`() {
        // given
        val first = User(42, "first")
        val second = User(42, "second")

        // when & then
        // known bug: the server assigns ids via Random().nextInt(100), so
        // collisions are likely, and nothing prevents two users from having
        // the same id
        assertNotSame(first, second)
        assertEquals(first.id, second.id)
    }

    @Test
    fun `test spaceships of users with the same id are indistinguishable on the game field`() {
        // given
        val first = Spaceship(Position(1, 1), User(42, "first"))
        val second = Spaceship(Position(2, 2), User(42, "second"))

        // when
        val firstView = first.view()
        val secondView = second.view()

        // then
        // known bug: user id is used as the spaceship view, so colliding ids
        // make two different ships look identical on the game field
        assertEquals(secondView, firstView)
    }
}
