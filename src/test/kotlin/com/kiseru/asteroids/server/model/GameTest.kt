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

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test onSpaceshipMove when there is a spaceship ahead`(direction: Direction) {
        // given
        val user1 = User(1, "Some cool username")
        val user2 = User(2, "Some cool an other username")
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, 3, 3)

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
        assertFailsWith<IllegalStateException>("Failed to move spaceship. There is an other spaceship ahead.") {
            game.onSpaceshipMove(spaceship)
        }
        assertEquals(spaceship.x, 2)
        assertEquals(spaceship.y, 2)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test onSpaceshipMove when there is no spaceship ahead`(direction: Direction) {
        // given
        val user = User(1, "Some cool username")
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, 3, 3)

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

    @Test
    fun `test damageSpaceship when the game is not started`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, 3, 3)

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(1, 1, user)

        // when & then
        assertFailsWith<IllegalStateException>("Game must have STARTED status") {
            game.damageSpaceship(spaceship, Type.SPACESHIP)
        }
    }

    @Test
    fun `test damageSpaceship when the spaceship crashed with an asteroid`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, 3, 3)
        game.status = GameStatus.STARTED

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(2, 2, user)

        // when
        game.damageSpaceship(spaceship, Type.ASTEROID)

        // then
        assertEquals(50, spaceship.score)
    }

    @Test
    fun `test damageSpaceship when the spaceship collected garbage`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, 3, 3)
        game.status = GameStatus.STARTED

        val asteroid = Asteroid(1, 1)
        game.addGameObject(asteroid)

        val garbage = Garbage(2, 1)
        game.addGameObject(garbage)

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(2, 2, user)

        // when
        game.damageSpaceship(spaceship, Type.GARBAGE)

        // then
        assertEquals(110, spaceship.score)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test damageSpaceship when the spaceship crashed with wall`(direction: Direction) {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, 3, 3)
        game.status = GameStatus.STARTED

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(2, 2, user)
        spaceship.direction = direction

        // when
        game.damageSpaceship(spaceship, Type.WALL)

        // then
        assertEquals(50, spaceship.score)
        val (expectedX, expectedY) = when (direction) {
            Direction.UP -> 2 to 3
            Direction.DOWN -> 2 to 1
            Direction.LEFT -> 3 to 2
            Direction.RIGHT -> 1 to 2
        }
        assertEquals(spaceship.x, expectedX)
        assertEquals(spaceship.y, expectedY)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test damageSpaceship when the spaceship crashed with an other spaceship`(direction: Direction) {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, 3, 3)
        game.status = GameStatus.STARTED

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(2, 2, user)
        spaceship.direction = direction

        // when
        game.damageSpaceship(spaceship, Type.SPACESHIP)

        // then
        assertEquals(50, spaceship.score)
        val (expectedX, expectedY) = when (direction) {
            Direction.UP -> 2 to 3
            Direction.DOWN -> 2 to 1
            Direction.LEFT -> 3 to 2
            Direction.RIGHT -> 1 to 2
        }
        assertEquals(spaceship.x, expectedX)
        assertEquals(spaceship.y, expectedY)
    }

    @Test
    fun `test damageSpaceship when the spaceship died after crashing with asteroid`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, 3, 3)
        game.status = GameStatus.STARTED

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(2, 2, user)
        spaceship.score = 0

        // when
        game.damageSpaceship(spaceship, Type.SPACESHIP)

        // then
        assertEquals(-50, spaceship.score)
        assertFalse(spaceship.isAlive)
        assertFalse(spaceship.isVisible)
    }

    @Test
    fun `test damageSpaceship when the spaceship collected last garbage`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, 3, 3)
        game.status = GameStatus.STARTED

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(2, 2, user)

        // when
        game.damageSpaceship(spaceship, Type.GARBAGE)

        // then
        assertEquals(110, spaceship.score)
        assertEquals(GameStatus.FINISHED, game.status)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isAsteroidAhead when an asteroid is ahead`(direction: Direction) {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, 3, 3)

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
        val game = Game(gameId, "Some cool game", 1, 3, 3)

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
        val game = Game(gameId, "Some cool game", 1, 3, 3)

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
        val game = Game(gameId, "Some cool game", 1, 3, 3)

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
        val game = Game(gameId, "Some cool game", 1, 1, 1)

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
        val game = Game(gameId, "Some cool game", 1, 3, 3)

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
        val game = Game(gameId, "Some cool game", 1, 1, 1)

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
        val game = Game(gameId, "Some cool game", 1, 1, 1)

        val garbage = Garbage(1, 1)
        game.addGameObject(garbage)

        // when & then
        assertFailsWith<IllegalStateException>("There is no free coordinate") {
            game.freeCoordinates()
                .first()
        }
    }
}