package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.model.GameObject.Asteroid
import com.kiseru.asteroids.server.model.GameObject.Garbage
import com.kiseru.asteroids.server.model.GameObject.Spaceship
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameTest {

    @Test
    fun `test onSpaceshipMove when the game isn't started`() {
        // given
        val gameField = GameField(3, 3)
        val game = Game(gameField)

        val player = Player()

        val spaceship = Spaceship(1, 1, "1")

        // when & then
        assertFailsWith<IllegalStateException> { game.onSpaceshipMove(player, spaceship) }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test onSpaceshipMove when there is a spaceship ahead`(direction: Direction) {
        // given
        val gameField = GameField(3, 3)
        val game = Game(gameField)

        val player1 = Player(direction = direction)

        val player2 = Player()

        val spaceship = Spaceship(2, 2, "1")
        game.addSpaceship(player1, spaceship) {}
        game.addSpaceship(player2, Spaceship(1, 1, "2")) {}
        game.addSpaceship(player2, Spaceship(2, 1, "3")) {}
        game.addSpaceship(player2, Spaceship(3, 1, "4")) {}
        game.addSpaceship(player2, Spaceship(1, 2, "5")) {}
        game.addSpaceship(player2, Spaceship(3, 2, "6")) {}
        game.addSpaceship(player2, Spaceship(1, 3, "7")) {}
        game.addSpaceship(player2, Spaceship(2, 3, "8")) {}
        game.addSpaceship(player2, Spaceship(3, 3, "9")) {}


        // when & then
        assertFailsWith<IllegalStateException> { game.onSpaceshipMove(player1, spaceship) }
        assertEquals(spaceship.x, 2)
        assertEquals(spaceship.y, 2)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test onSpaceshipMove when there is no spaceship ahead`(direction: Direction) {
        // given
        val gameField = GameField(3, 3)
        val game = Game(gameField)

        val player = Player(direction = direction)

        val spaceship = Spaceship(2, 2, "1")
        game.addSpaceship(player, spaceship) {}

        // when
        game.onSpaceshipMove(player, spaceship)

        // then
        val (expectedX, expectedY) = when (direction) {
            Direction.UP -> 2 to 1
            Direction.DOWN -> 2 to 3
            Direction.LEFT -> 1 to 2
            Direction.RIGHT -> 3 to 2
        }
        assertEquals(spaceship.x, expectedX)
        assertEquals(spaceship.y, expectedY)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isAsteroidAhead when an asteroid is ahead`(direction: Direction) {
        // given
        val gameField = GameField(3, 3)
        val game = Game(gameField)

        listOf(
            1 to 1,
            1 to 3,
            3 to 1,
            3 to 3,
        )
            .forEach { (x, y) ->
                val garbage = Garbage(x, y)
                game.addGameObject(garbage)
            }

        listOf(
            1 to 2,
            2 to 1,
            2 to 3,
            3 to 2,
        )
            .forEach { (x, y) ->
                val asteroid = Asteroid(x, y)
                game.addGameObject(asteroid)
            }

        val spaceship = Spaceship(2, 2, "1")

        // when
        val actual = game.isAsteroidAhead(direction, spaceship)

        // then
        assertTrue(actual)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isAsteroidAhead when an asteroid isn't ahead`(direction: Direction) {
        // given
        val gameField = GameField(3, 3)
        val game = Game(gameField)

        listOf(
            1 to 2,
            2 to 3,
            2 to 1,
            3 to 2,
        )
            .forEach { (x, y) ->
                val garbage = Garbage(x, y)
                game.addGameObject(garbage)
            }

        listOf(
            1 to 1,
            1 to 3,
            3 to 1,
            3 to 3,
        )
            .forEach { (x, y) ->
                val asteroid = Asteroid(x, y)
                game.addGameObject(asteroid)
            }

        val spaceship = Spaceship(2, 2, "1")

        // when
        val actual = game.isAsteroidAhead(direction, spaceship)

        // then
        assertFalse(actual)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isAsteroidAhead when garbage is ahead`(direction: Direction) {
        // given
        val gameField = GameField(3, 3)
        val game = Game(gameField)

        listOf(
            1 to 1,
            1 to 3,
            3 to 1,
            3 to 3,
        )
            .forEach { (x, y) ->
                val asteroid = Asteroid(x, y)
                game.addGameObject(asteroid)
            }

        listOf(
            1 to 2,
            2 to 1,
            2 to 3,
            3 to 2,
        )
            .forEach { (x, y) ->
                val garbage = Garbage(x, y)
                game.addGameObject(garbage)
            }

        val spaceship = Spaceship(2, 2, "1")

        // when
        val actual = game.isGarbageAhead(direction, spaceship)

        // then
        assertTrue(actual)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isAsteroidAhead when garbage isn't ahead`(direction: Direction) {
        // given
        val gameField = GameField(3, 3)
        val game = Game(gameField)

        listOf(
            1 to 2,
            2 to 3,
            2 to 1,
            3 to 2,
        )
            .forEach { (x, y) ->
                val asteroid = Asteroid(x, y)
                game.addGameObject(asteroid)
            }

        listOf(
            1 to 1,
            1 to 3,
            3 to 1,
            3 to 3,
        )
            .forEach { (x, y) ->
                val garbage = Garbage(x, y)
                game.addGameObject(garbage)
            }

        val spaceship = Spaceship(2, 2, "1")

        // when
        val actual = game.isGarbageAhead(direction, spaceship)

        // then
        assertFalse(actual)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isWallAhead when the wall is ahead`(direction: Direction) {
        // given
        val gameField = GameField(1, 1)
        val game = Game(gameField)

        val spaceship = Spaceship(1, 1, "1")

        // when
        val actual = game.isWallAhead(direction, spaceship)

        // then
        assertTrue(actual)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isWallAhead when the wall isn't ahead`(direction: Direction) {
        // given
        val gameField = GameField(3, 3)
        val game = Game(gameField)

        val spaceship = Spaceship(2, 2, "1")

        // when
        val actual = game.isWallAhead(direction, spaceship)

        // then
        assertFalse(actual)
    }

    @Test
    fun `test freeCoordinates when there are free coordinates`() {
        // given
        val gameField = GameField(1, 1)
        val game = Game(gameField)

        // when
        val (actualX, actualY) = game.freeCoordinates()
            .first()

        // then
        assertEquals(1, actualX)
        assertEquals(1, actualY)
    }

    @Test
    fun `test freeCoordinates when there are no free coordinates`() {
        // given
        val gameField = GameField(1, 1)
        val game = Game(gameField)

        val garbage = Garbage(1, 1)
        game.addGameObject(garbage)

        // when & then
        assertFailsWith<IllegalStateException>("There is no free coordinate") {
            game.freeCoordinates()
                .first()
        }
    }

    @Test
    fun `test getPlayers`() {
        // given
        val gameField = GameField(1, 1)
        val game = Game(gameField)

        val player = Player()

        val spaceship = Spaceship(1, 1, "1")
        game.addSpaceship(player, spaceship) {}

        // when
        val spaceships = game.getPlayers()

        // then
        assertEquals(1, spaceships.size)
        assertEquals(spaceships.first(), player to spaceship)
    }

    @Test
    fun `test getSendMessagesHandlers`() {
        // given
        val gameField = GameField(1, 1)
        val game = Game(gameField)

        val player = Player()

        val spaceship = Spaceship(1, 1, "1")
        val onMessageSend: (String) -> Unit = {}
        game.addSpaceship(player, spaceship, onMessageSend)

        // when
        val handlers = game.getSendMessageHandlers()

        // then
        assertEquals(1, handlers.size)
        assertEquals(handlers.first(), onMessageSend)
    }
}