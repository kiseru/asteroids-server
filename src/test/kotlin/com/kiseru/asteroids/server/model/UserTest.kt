package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.service.impl.GameServiceImpl
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
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
        val gameService = GameServiceImpl()
        val firstGame = Game(java.util.UUID.randomUUID(), "first", 1, FieldSize(1, 1))
        firstGame.createSpaceship(User(42, "first"))
        val secondGame = Game(java.util.UUID.randomUUID(), "second", 1, FieldSize(1, 1))
        secondGame.createSpaceship(User(42, "second"))
        val firstOutputStream = ByteArrayOutputStream()
        val secondOutputStream = ByteArrayOutputStream()

        // when
        gameService.writeGameField(firstGame, firstOutputStream)
        gameService.writeGameField(secondGame, secondOutputStream)

        // then
        // known bug: user id is used as the spaceship view, so colliding ids
        // make two different ships look identical on the game field
        assertEquals(secondOutputStream.toString(), firstOutputStream.toString())
    }
}
