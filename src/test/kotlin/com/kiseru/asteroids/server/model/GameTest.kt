package com.kiseru.asteroids.server.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameTest {

    @Test
    fun `test onSpaceshipMove when the game isn't started`() {
        // given
        val gameId = UUID.randomUUID()
        val gameField = GameField(3, 3)
        val game = Game(gameId, "Some cool game", 1, gameField)

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(1, 1, user)

        // when & then
        assertFailsWith<IllegalStateException> { game.onSpaceshipMove(spaceship) }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test onSpaceshipMove when there is a spaceship ahead`(direction: Direction) {
        // given
        val user1 = User(1, "Some cool username")
        val user2 = User(2, "Some cool an other username")
        val gameId = UUID.randomUUID()
        val gameField = GameField(3, 3)
        val game = Game(gameId, "Some cool game", 1, gameField)
        game.status = GameStatus.STARTED

        val spaceship = Spaceship(2, 2, user1)
        spaceship.direction = direction
        game.addSpaceship(spaceship) {}
        game.addSpaceship(Spaceship(1, 1, user2)) {}
        game.addSpaceship(Spaceship(2, 1, user2)) {}
        game.addSpaceship(Spaceship(3, 1, user2)) {}
        game.addSpaceship(Spaceship(1, 2, user2)) {}
        game.addSpaceship(Spaceship(3, 2, user2)) {}
        game.addSpaceship(Spaceship(1, 3, user2)) {}
        game.addSpaceship(Spaceship(2, 3, user2)) {}
        game.addSpaceship(Spaceship(3, 3, user2)) {}


        // when & then
        assertFailsWith<IllegalStateException> { game.onSpaceshipMove(spaceship) }
        assertEquals(spaceship.x, 2)
        assertEquals(spaceship.y, 2)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test onSpaceshipMove when there is no spaceship ahead`(direction: Direction) {
        // given
        val user = User(1, "Some cool username")
        val gameId = UUID.randomUUID()
        val gameField = GameField(3, 3)
        val game = Game(gameId, "Some cool game", 1, gameField)
        game.status = GameStatus.STARTED

        val spaceship = Spaceship(2, 2, user)
        spaceship.direction = direction
        game.addSpaceship(spaceship) {}

        // when
        game.onSpaceshipMove(spaceship)

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
        val gameId = UUID.randomUUID()
        val gameField = GameField(3, 3)
        val game = Game(gameId, "Some cool game", 1, gameField)

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

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(2, 2, user)
        spaceship.direction = direction

        // when
        val actual = game.isAsteroidAhead(spaceship)

        // then
        assertTrue(actual)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isAsteroidAhead when an asteroid isn't ahead`(direction: Direction) {
        // given
        val gameId = UUID.randomUUID()
        val gameField = GameField(3, 3)
        val game = Game(gameId, "Some cool game", 1, gameField)

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

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(2, 2, user)
        spaceship.direction = direction

        // when
        val actual = game.isAsteroidAhead(spaceship)

        // then
        assertFalse(actual)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isAsteroidAhead when garbage is ahead`(direction: Direction) {
        // given
        val gameId = UUID.randomUUID()
        val gameField = GameField(3, 3)
        val game = Game(gameId, "Some cool game", 1, gameField)

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

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(2, 2, user)
        spaceship.direction = direction

        // when
        val actual = game.isGarbageAhead(spaceship)

        // then
        assertTrue(actual)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isAsteroidAhead when garbage isn't ahead`(direction: Direction) {
        // given
        val gameId = UUID.randomUUID()
        val gameField = GameField(3, 3)
        val game = Game(gameId, "Some cool game", 1, gameField)

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

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(2, 2, user)
        spaceship.direction = direction

        // when
        val actual = game.isGarbageAhead(spaceship)

        // then
        assertFalse(actual)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isWallAhead when the wall is ahead`(direction: Direction) {
        // given
        val gameId = UUID.randomUUID()
        val gameField = GameField(1, 1)
        val game = Game(gameId, "Some cool game", 1, gameField)

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(1, 1, user)
        spaceship.direction = direction

        // when
        val actual = game.isWallAhead(spaceship)

        // then
        assertTrue(actual)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isWallAhead when the wall isn't ahead`(direction: Direction) {
        // given
        val gameId = UUID.randomUUID()
        val gameField = GameField(3, 3)
        val game = Game(gameId, "Some cool game", 1, gameField)

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(2, 2, user)
        spaceship.direction = direction

        // when
        val actual = game.isWallAhead(spaceship)

        // then
        assertFalse(actual)
    }

    @Test
    fun `test freeCoordinates when there are free coordinates`() {
        // given
        val gameId = UUID.randomUUID()
        val gameField = GameField(1, 1)
        val game = Game(gameId, "Some cool game", 1, gameField)

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
        val gameId = UUID.randomUUID()
        val gameField = GameField(1, 1)
        val game = Game(gameId, "Some cool game", 1, gameField)

        val garbage = Garbage(1, 1)
        game.addGameObject(garbage)

        // when & then
        assertFailsWith<IllegalStateException>("There is no free coordinate") {
            game.freeCoordinates()
                .first()
        }
    }

    @Test
    fun `test getSpaceships`() {
        // given
        val gameId = UUID.randomUUID()
        val gameField = GameField(1, 1)
        val game = Game(gameId, "Some cool game", 1, gameField)

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(1, 1, user)
        game.addSpaceship(spaceship) {}

        // when
        val spaceships = game.getSpaceships()

        // then
        assertEquals(1, spaceships.size)
        assertEquals(spaceships.first(), spaceship)
    }

    @Test
    fun `test getSendMessagesHandlers`() {
        // given
        val gameId = UUID.randomUUID()
        val gameField = GameField(1, 1)
        val game = Game(gameId, "Some cool game", 1, gameField)

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(1, 1, user)
        val onMessageSend: (String) -> Unit = {}
        game.addSpaceship(spaceship, onMessageSend)

        // when
        val handlers = game.getSendMessageHandlers()

        // then
        assertEquals(1, handlers.size)
        assertEquals(handlers.first(), onMessageSend)
    }
}