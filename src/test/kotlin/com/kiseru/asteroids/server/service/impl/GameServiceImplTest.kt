package com.kiseru.asteroids.server.service.impl

import com.kiseru.asteroids.server.model.FieldSize
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.User
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.util.UUID
import kotlin.test.assertEquals

class GameServiceImplTest {

    @Test
    fun `test writeRatings when no games are registered`() {
        // given
        val gameService = GameServiceImpl()
        val outputStream = ByteArrayOutputStream()

        // when
        gameService.writeRatings(outputStream)

        // then
        // known bug: no game is ever registered in the service, so the
        // RATING system command always writes nothing
        assertEquals("", outputStream.toString())
    }

    @Test
    fun `test writeGameFields when no games are registered`() {
        // given
        val gameService = GameServiceImpl()
        val outputStream = ByteArrayOutputStream()

        // when
        gameService.writeGameFields(outputStream)

        // then
        // known bug: no game is ever registered in the service, so the
        // GAME_FIELD system command always writes nothing
        assertEquals("", outputStream.toString())
    }

    @Test
    fun `test writeRatings when a game exists outside of the service`() {
        // given
        val gameService = GameServiceImpl()
        val outputStream = ByteArrayOutputStream()

        val game = Game(UUID.randomUUID(), "Some cool game", 1, FieldSize(3, 3))
        game.createSpaceship(User(1, "Some cool username"))

        // when
        gameService.writeRatings(outputStream)

        // then
        // known bug: games are created per connection and never registered
        // in the service, so even an existing game is not reflected in the output
        assertEquals("", outputStream.toString())
    }
}
