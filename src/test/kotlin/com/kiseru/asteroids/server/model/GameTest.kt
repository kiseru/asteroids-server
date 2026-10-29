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

        val spaceship = game.createSpaceship(user)

        // when & then
        assertFailsWith<IllegalStateException> { game.onSpaceshipMove(spaceship) }
    }

    @Test
    fun `test onSpaceshipMove when spaceship does not belong to the game`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val spaceship = Spaceship(Position(1, 1), User(1, "Some cool username"))

        game.start()

        // when & then
        assertFailsWith<IllegalArgumentException> { game.onSpaceshipMove(spaceship) }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test isWallAhead when the wall is ahead`(direction: Direction) {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(1, 1))

        val user = User(1, "Some cool username")

        val spaceship = game.createSpaceship(user)
        spaceship.changeDirection(direction)

        // when
        val actual = game.isWallAhead(spaceship)

        // then
        assertTrue(actual)
    }

    @Test
    fun `test getSpaceships`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(1, 1))

        val user = User(1, "Some cool username")

        val spaceship = game.createSpaceship(user)

        // when
        val spaceships = game.getSpaceships()

        // then
        assertEquals(1, spaceships.size)
        assertEquals(spaceships.first(), spaceship)
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
    fun `test removeGameObject when game isn't started`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))
        game.generateGarbage(1)

        val gameObject = game.getGameObjects()
            .single()

        // when & then
        assertThrows<IllegalStateException> { game.removeGameObject(gameObject) }
    }

    @Test
    fun `test removeGameObject when game object does not belong to the game`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val gameObject = Garbage(Position(2, 2))

        // when & then
        assertThrows<IllegalStateException> { game.removeGameObject(gameObject) }
    }

    @Test
    fun `test removeGameObject when status is started`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))
        game.generateGarbage(1)
        game.start()

        val gameObject = game.getGameObjects()
            .single()

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
        game.generateGarbage(1)

        val gameObject = game.getGameObjects().single()

        // when
        val actual = game.findCollidableObjectAt(gameObject.position)

        // then
        assertNotNull(actual)
    }

    @Test
    fun `test findCollidableObjectAt when position has spaceship`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))
        val user = User(1, "Some cool name")
        val spaceship = game.createSpaceship(user)

        // when
        val actual = game.findCollidableObjectAt(spaceship.position)

        // then
        assertNull(actual)
    }

    @Test
    fun `test damageSpaceship when game isn't started`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val spaceship = Spaceship(Position(2, 2), User(1, "Some cool name"))

        // when & then
        assertThrows<IllegalStateException> { game.damageSpaceship(spaceship, Type.ASTEROID) }
    }

    @Test
    fun `test damageSpaceship when spaceship does not belong to the game`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val spaceship = Spaceship(Position(2, 2), User(1, "Some cool name"))

        game.start()

        // when & then
        assertThrows<IllegalArgumentException> { game.damageSpaceship(spaceship, Type.ASTEROID) }
    }

    @Test
    fun `test damageSpaceship when type is ASTEROID`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val spaceship = game.createSpaceship(User(1, "Some cool name"))

        game.start()

        // when
        game.damageSpaceship(spaceship, Type.ASTEROID)

        // then
        assertEquals(Score(50), spaceship.score)
    }

    @Test
    fun `test damageSpaceship when type is GARBAGE and after that garbage exists`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))
        game.generateGarbage(1)

        val spaceship = game.createSpaceship(User(1, "Some cool name"))

        game.start()

        // when
        game.damageSpaceship(spaceship, Type.GARBAGE)

        // then
        assertEquals(Game.Status.STARTED, game.status)
        assertEquals(Score(110), spaceship.score)
    }

    @Test
    fun `test damageSpaceship when type is GARBAGE and after that garbage doesn't exist`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val spaceship = game.createSpaceship(User(1, "Some cool name"))

        game.start()

        // when
        game.damageSpaceship(spaceship, Type.GARBAGE)

        // then
        assertEquals(Game.Status.FINISHED, game.status)
        assertEquals(Score(110), spaceship.score)
    }

    @Test
    fun `test damageSpaceship when type is WALL`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val spaceship = game.createSpaceship(User(1, "Some cool name"))

        game.start()

        // when
        game.damageSpaceship(spaceship, Type.WALL)

        // then
        assertEquals(Score(50), spaceship.score)
    }

    @Test
    fun `test damageSpaceship when type is SPACESHIP`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val spaceship = game.createSpaceship(User(1, "Some cool name"))

        game.start()

        // when
        game.damageSpaceship(spaceship, Type.SPACESHIP)

        // then
        assertEquals(Score(50), spaceship.score)
    }

    @Test
    fun `test destroySpaceship when game isn't started`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val spaceship = game.createSpaceship(User(1, "Some cool name"))

        // when & then
        assertThrows<IllegalStateException> { game.destroySpaceship(spaceship) }
    }

    @Test
    fun `test destroySpaceship when spaceship is already destroyed`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val spaceship = game.createSpaceship(User(1, "Some cool name"))
        spaceship.destroy()

        game.start()

        // when & then
        assertThrows<IllegalStateException> { game.destroySpaceship(spaceship) }
    }

    @Test
    fun `test destroySpaceship when spaceship does not belong to the game`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))
        game.start()

        val spaceship = Spaceship(Position(2, 2), User(1, "Some cool name"))

        // when & then
        assertThrows<IllegalArgumentException> { game.destroySpaceship(spaceship) }
    }

    @Test
    fun `test destroySpaceship`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val spaceship = game.createSpaceship(User(1, "Some cool name"))

        game.start()

        // when
        game.destroySpaceship(spaceship)

        // then
        assertEquals(Spaceship.Status.DESTROYED, spaceship.status)
        val searchResult = game.getGameObjects()
            .firstOrNull { it == spaceship }
        assertNull(searchResult)
    }

    @Test
    fun `test generateAsteroids when game isn't created`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))
        game.start()

        // when & then
        assertThrows<IllegalStateException> { game.generateAsteroids(3) }
    }

    @Test
    fun `test generateAsteroids when count is less than 1`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        // when & then
        assertThrows<IllegalArgumentException> { game.generateAsteroids(0) }
    }

    @Test
    fun `test generateAsteroids when count is greater than free positions`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        // when & then
        assertThrows<IllegalStateException> { game.generateAsteroids(10) }
    }

    @Test
    fun `test generateAsteroids`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        // when
        game.generateAsteroids(3)

        // then
        val gameObjects = game.getGameObjects()
        assertEquals(3, gameObjects.size)

        assertEquals(3, gameObjects.count { it.type == Type.ASTEROID })
    }

    @Test
    fun `test generateGarbage when game isn't created`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))
        game.start()

        // when & then
        assertThrows<IllegalStateException> { game.generateGarbage(3) }
    }

    @Test
    fun `test generateGarbage when count is less than 1`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        // when & then
        assertThrows<IllegalArgumentException> { game.generateGarbage(0) }
    }

    @Test
    fun `test generateGarbage when count is greater than free positions`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        // when & then
        assertThrows<IllegalStateException> { game.generateGarbage(10) }
    }

    @Test
    fun `test generateGarbage`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        // when
        game.generateGarbage(3)

        // then
        val gameObjects = game.getGameObjects()
        assertEquals(3, gameObjects.size)

        assertEquals(3, gameObjects.count { it.type == Type.GARBAGE })
    }

    @Test
    fun `test createSpaceship when game isn't created`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))
        game.start()

        val user = User(1, "Some cool user")

        // when & then
        assertThrows<IllegalStateException> { game.createSpaceship(user) }
    }

    @Test
    fun `test createSpaceship`() {
        // given
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, FieldSize(3, 3))

        val user = User(1, "Some cool user")

        // when
        val spaceship = game.createSpaceship(user)

        // then
        val spaceships = game.getGameObjects()
        assertEquals(1, spaceships.size)
        assertTrue(spaceships.contains(spaceship))

        val gameObjects = game.getGameObjects()
        assertEquals(1, gameObjects.size)
        assertTrue(gameObjects.contains(spaceship))
    }
}
