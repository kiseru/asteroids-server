package com.kiseru.asteroids.server.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameTest {

    @Test
    fun `test onSpaceshipMove when the game isn't started`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(Position(1, 1), user)

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
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))
        game.start()

        val spaceship = Spaceship(Position(2, 2), user1)
        spaceship.changeDirection(direction)
        game.addSpaceship(spaceship) {}
        game.addSpaceship(Spaceship(Position(1, 1), user2)) {}
        game.addSpaceship(Spaceship(Position(2, 1), user2)) {}
        game.addSpaceship(Spaceship(Position(3, 1), user2)) {}
        game.addSpaceship(Spaceship(Position(1, 2), user2)) {}
        game.addSpaceship(Spaceship(Position(3, 2), user2)) {}
        game.addSpaceship(Spaceship(Position(1, 3), user2)) {}
        game.addSpaceship(Spaceship(Position(2, 3), user2)) {}
        game.addSpaceship(Spaceship(Position(3, 3), user2)) {}


        // when & then
        assertFailsWith<IllegalStateException> { game.onSpaceshipMove(spaceship) }
        assertEquals(Position(2, 2), spaceship.position)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test onSpaceshipMove when there is no spaceship ahead`(direction: Direction) {
        // given
        val user = User(1, "Some cool username")
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))
        game.start()

        val spaceship = Spaceship(Position(2, 2), user)
        spaceship.changeDirection(direction)
        game.addSpaceship(spaceship) {}

        // when
        game.onSpaceshipMove(spaceship)

        // then
        val expectedPosition = when (direction) {
            Direction.UP -> Position(2, 1)
            Direction.DOWN -> Position(2, 3)
            Direction.LEFT -> Position(1, 2)
            Direction.RIGHT -> Position(3, 2)
        }
        assertEquals(expectedPosition, spaceship.position)
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isAsteroidAhead when an asteroid is ahead`(direction: Direction) {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        listOf(
            1 to 1,
            1 to 3,
            3 to 1,
            3 to 3,
        )
            .forEach { (x, y) ->
                val garbage = Garbage(Position(x, y))
                game.addGameObject(garbage)
            }

        listOf(
            1 to 2,
            2 to 1,
            2 to 3,
            3 to 2,
        )
            .forEach { (x, y) ->
                val asteroid = Asteroid(Position(x, y))
                game.addGameObject(asteroid)
            }

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(Position(2, 2), user)
        spaceship.changeDirection(direction)

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
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        listOf(
            1 to 2,
            2 to 3,
            2 to 1,
            3 to 2,
        )
            .forEach { (x, y) ->
                val garbage = Garbage(Position(x, y))
                game.addGameObject(garbage)
            }

        listOf(
            1 to 1,
            1 to 3,
            3 to 1,
            3 to 3,
        )
            .forEach { (x, y) ->
                val asteroid = Asteroid(Position(x, y))
                game.addGameObject(asteroid)
            }

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(Position(2, 2), user)
        spaceship.changeDirection(direction)

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
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        listOf(
            1 to 1,
            1 to 3,
            3 to 1,
            3 to 3,
        )
            .forEach { (x, y) ->
                val asteroid = Asteroid(Position(x, y))
                game.addGameObject(asteroid)
            }

        listOf(
            1 to 2,
            2 to 1,
            2 to 3,
            3 to 2,
        )
            .forEach { (x, y) ->
                val garbage = Garbage(Position(x, y))
                game.addGameObject(garbage)
            }

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(Position(2, 2), user)
        spaceship.changeDirection(direction)

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
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        listOf(
            1 to 2,
            2 to 3,
            2 to 1,
            3 to 2,
        )
            .forEach { (x, y) ->
                val asteroid = Asteroid(Position(x, y))
                game.addGameObject(asteroid)
            }

        listOf(
            1 to 1,
            1 to 3,
            3 to 1,
            3 to 3,
        )
            .forEach { (x, y) ->
                val garbage = Garbage(Position(x, y))
                game.addGameObject(garbage)
            }

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(Position(2, 2), user)
        spaceship.changeDirection(direction)

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
        val game = Game(gameId, "Some cool game", 1, FieldSize(1, 1))

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(Position(1, 1), user)
        spaceship.changeDirection(direction)

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
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(Position(2, 2), user)
        spaceship.changeDirection(direction)

        // when
        val actual = game.isWallAhead(spaceship)

        // then
        assertFalse(actual)
    }

    @Test
    fun `test freeCoordinates when there are free coordinates`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(1, 1))

        // when
        val position = game.freeCoordinates()
            .first()

        // then
        assertEquals(Position(1, 1), position)
    }

    @Test
    fun `test freeCoordinates when there are no free coordinates`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(1, 1))

        val garbage = Garbage(Position(1, 1))
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
        val game = Game(gameId, "Some cool game", 1, FieldSize(1, 1))

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(Position(1, 1), user)
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
        val game = Game(gameId, "Some cool game", 1, FieldSize(1, 1))

        val user = User(1, "Some cool username")

        val spaceship = Spaceship(Position(1, 1), user)
        val onMessageSend: (String) -> Unit = {}
        game.addSpaceship(spaceship, onMessageSend)

        // when
        val handlers = game.getSendMessageHandlers()

        // then
        assertEquals(1, handlers.size)
        assertEquals(handlers.first(), onMessageSend)
    }

    @Test
    fun `test start when game is in created status`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(1, 1))

        // when
        game.start()

        assertEquals(Game.Status.STARTED, game.status)
    }

    @Test
    fun `test start when game is in started status`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(1, 1))
        game.start()

        // when & then
        assertThrows<IllegalStateException> { game.start() }
        assertEquals(Game.Status.STARTED, game.status)
    }

    @Test
    fun `test start when game is in finished status`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(1, 1))
        game.start()
        game.finish()

        // when & then
        assertThrows<IllegalStateException> { game.start() }
        assertEquals(Game.Status.FINISHED, game.status)
    }

    @Test
    fun `test finish when game is in created status`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(1, 1))

        // when & then
        assertThrows<IllegalStateException> { game.finish() }
        assertEquals(Game.Status.CREATED, game.status)
    }

    @Test
    fun `test finish when game is in started status`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(1, 1))
        game.start()

        // when
        game.finish()

        // then
        assertEquals(Game.Status.FINISHED, game.status)

    }

    @Test
    fun `test finish when game is in finished status`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(1, 1))
        game.start()
        game.finish()

        // when & then
        assertThrows<IllegalStateException> { game.start() }
        assertEquals(Game.Status.FINISHED, game.status)
    }

    @Test
    fun `test removeGameObject when status is started`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))
        val gameObject = Garbage(Position(2, 2))
        game.addGameObject(gameObject)
        game.start()

        // when
        game.removeGameObject(gameObject)

        // then
        assertFalse(game.getGameObjects().contains(gameObject))
    }

    @Test
    fun `test findCollidableObjectAt when position is empty`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        // when
        val actual = game.findCollidableObjectAt(Position(2, 2))

        // then
        assertNull(actual)
    }

    @Test
    fun `test findCollidableObjectAt when position has garbage`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))
        game.addGameObject(Garbage(Position(2, 2)))

        // when
        val actual = game.findCollidableObjectAt(Position(2, 2))

        // then
        assertNotNull(actual)
    }

    @Test
    fun `test findCollidableObjectAt when position has spaceship`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))
        val user = User(1, "Some cool name")
        game.addSpaceship(Spaceship(Position(2, 2), user)) {}

        // when
        val actual = game.findCollidableObjectAt(Position(2, 2))

        // then
        assertNull(actual)
    }
}
