package com.kiseru.asteroids.server.model

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GameTest {

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `test onSpaceshipMove when there is a spaceship ahead`(direction: Direction) {
        // given
        val user1 = User(1, "Some cool username")
        val user2 = User(2, "Some cool an other username")
        val gameId = UUID.randomUUID()
        val game = Game(gameId, "Some cool game", 1, 3, 3, 1)

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
        val game = Game(gameId, "Some cool game", 1, 3, 3, 1)

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
}