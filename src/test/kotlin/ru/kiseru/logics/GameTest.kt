package ru.kiseru.logics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.kiseru.logics.auxiliary.Type
import ru.kiseru.logics.handlers.SpaceshipCrashHandler
import ru.kiseru.logics.models.Asteroid
import ru.kiseru.logics.models.Garbage
import ru.kiseru.logics.models.Spaceship
import ru.kiseru.server.User
import java.util.NoSuchElementException

class GameTest {

    @Test
    fun `given screen and counts - when game created - then garbage and asteroids generated`() {
        // given
        val screen = Screen(10, 10)

        // when
        val game = Game(screen, 3, 2)

        // then
        assertEquals(3, game.pointsOnScreen.count { it.type == Type.GARBAGE })
        assertEquals(2, game.pointsOnScreen.count { it.type == Type.ASTEROID })
    }

    @Test
    fun `given screen and counts - when game created - then game objects contain the same points as screen`() {
        // given
        val screen = Screen(10, 10)

        // when
        val game = Game(screen, 2, 2)

        // then
        assertEquals(game.pointsOnScreen, game.gameObjects)
    }

    @Test
    fun `given screen and counts - when game created - then all coordinates unique and within screen bounds`() {
        // given
        val screen = Screen(10, 10)

        // when
        val game = Game(screen, 4, 3)

        // then
        val coordinates = game.pointsOnScreen.map { it.coordinates }
        assertEquals(coordinates.size, coordinates.distinct().size)
        assertTrue(coordinates.all { it.x in 1..screen.width && it.y in 1..screen.height })
    }

    @Test
    fun `given screen and counts - when game created - then garbage number stored`() {
        // given
        val screen = Screen(10, 10)

        // when
        val game = Game(screen, 7, 1)

        // then
        assertEquals(7, game.garbageNumber)
    }

    @Test
    fun `given screen - when game created without garbage and asteroids - then points list is empty`() {
        // given
        val screen = Screen(10, 10)

        // when
        val game = Game(screen, 0, 0)

        // then
        assertTrue(game.pointsOnScreen.isEmpty())
        assertTrue(game.gameObjects.isEmpty())
    }

    @Test
    fun `given screen - when game created - then screen stored`() {
        // given
        val screen = Screen(10, 10)

        // when
        val game = Game(screen, 1, 1)

        // then
        assertSame(screen, game.screen)
    }

    @Test
    fun `given user - when spaceship registered - then spaceship passed to user`() {
        // given
        val game = Game(Screen(10, 10), 1, 1)
        val user = mock<User>()

        // when
        game.registerSpaceShipForUser(user)

        // then
        val captor = argumentCaptor<Spaceship>()
        verify(user).setSpaceShip(captor.capture())
        val spaceship = captor.firstValue
        assertEquals(Type.SPACESHIP, spaceship.type)
    }

    @Test
    fun `given user - when spaceship registered - then spaceship added to points on screen`() {
        // given
        val game = Game(Screen(10, 10), 2, 2)
        val user = mock<User>()

        // when
        game.registerSpaceShipForUser(user)

        // then
        val captor = argumentCaptor<Spaceship>()
        verify(user).setSpaceShip(captor.capture())
        assertTrue(game.pointsOnScreen.contains(captor.firstValue))
    }

    @Test
    fun `given user - when spaceship registered - then crash handler created`() {
        // given
        val game = Game(Screen(10, 10), 1, 1)
        val user = mock<User>()

        // when
        game.registerSpaceShipForUser(user)

        // then
        assertEquals(1, game.crashHandlers.size)
    }

    @Test
    fun `given user - when spaceship registered - then course checker set to spaceship`() {
        // given
        val game = Game(Screen(10, 10), 1, 1)
        val user = mock<User>()

        // when
        game.registerSpaceShipForUser(user)

        // then
        val captor = argumentCaptor<Spaceship>()
        verify(user).setSpaceShip(captor.capture())
        assertNotNull(captor.firstValue.courseChecker)
    }

    @Test
    fun `given user - when spaceship registered - then spaceship not collide with other points`() {
        // given
        val game = Game(Screen(10, 10), 4, 4)
        val user = mock<User>()

        // when
        game.registerSpaceShipForUser(user)

        // then
        val captor = argumentCaptor<Spaceship>()
        verify(user).setSpaceShip(captor.capture())
        val spaceshipCoordinates = captor.firstValue.coordinates
        val otherCoordinates = game.pointsOnScreen
            .filter { it.type != Type.SPACESHIP }
            .map { it.coordinates }
        assertFalse(otherCoordinates.contains(spaceshipCoordinates))
    }

    @Test
    fun `given game - when refresh called - then screen updated`() {
        // given
        val screen = mock<Screen>()
        whenever(screen.width).thenReturn(10)
        whenever(screen.height).thenReturn(10)
        val game = Game(screen, 1, 1)

        // when
        game.refresh()

        // then
        verify(screen).update()
    }

    @Test
    fun `given game with crash handlers - when refresh called - then all crash handlers checked`() {
        // given
        val screen = mock<Screen>()
        whenever(screen.width).thenReturn(10)
        whenever(screen.height).thenReturn(10)
        val game = Game(screen, 1, 1)
        val crashHandler = mock<SpaceshipCrashHandler>()
        game.crashHandlers.add(crashHandler)

        // when
        game.refresh()

        // then
        verify(crashHandler).check()
    }

    @Test
    fun `given game with garbage and asteroid - when refresh called - then objects rendered on screen`() {
        // given
        val screen = Screen(10, 10)
        val game = Game(screen, 1, 1)
        val garbage = game.gameObjects.filterIsInstance<Garbage>().first()
        val asteroid = game.gameObjects.filterIsInstance<Asteroid>().first()

        // when
        game.refresh()

        // then
        val lines = screen.display().split("\n")
        assertEquals("G", lines[garbage.y - 1].split("\t")[garbage.x - 1])
        assertEquals("A", lines[asteroid.y - 1].split("\t")[asteroid.x - 1])
    }

    @Test
    fun `given spaceship with alive owner - when isAnyoneAlive called - then true returned`() {
        // given
        val game = Game(Screen(10, 10), 1, 1)
        val user = mock<User>()
        whenever(user.isAlive()).thenReturn(true)
        game.registerSpaceShipForUser(user)

        // when
        val actual = game.isAnyoneAlive()

        // then
        assertTrue(actual)
    }

    @Test
    fun `given spaceship with dead owner - when isAnyoneAlive called - then false returned`() {
        // given
        val game = Game(Screen(10, 10), 1, 1)
        val user = mock<User>()
        whenever(user.isAlive()).thenReturn(false)
        game.registerSpaceShipForUser(user)

        // when
        val actual = game.isAnyoneAlive()

        // then
        assertFalse(actual)
    }

    @Test
    fun `given two users one alive - when isAnyoneAlive called - then true returned`() {
        // given
        val game = Game(Screen(10, 10), 1, 1)
        val deadUser = mock<User>()
        whenever(deadUser.isAlive()).thenReturn(false)
        val aliveUser = mock<User>()
        whenever(aliveUser.isAlive()).thenReturn(true)
        game.registerSpaceShipForUser(deadUser)
        game.registerSpaceShipForUser(aliveUser)

        // when
        val actual = game.isAnyoneAlive()

        // then
        assertTrue(actual)
    }

    @Test
    fun `given game without spaceships - when isAnyoneAlive called - then exception thrown`() {
        // given
        val game = Game(Screen(10, 10), 1, 1)

        // when & then
        assertThrows<NoSuchElementException>(game::isAnyoneAlive)
    }

    @Test
    fun `given game - when increment called first time - then one returned`() {
        // given
        val game = Game(Screen(10, 10), 1, 1)

        // when
        val actual = game.incrementCollectedGarbageCount()

        // then
        assertEquals(1, actual)
    }

    @Test
    fun `given game - when increment called twice - then counters incremented sequentially`() {
        // given
        val game = Game(Screen(10, 10), 1, 1)

        // when
        val first = game.incrementCollectedGarbageCount()
        val second = game.incrementCollectedGarbageCount()

        // then
        assertEquals(1, first)
        assertEquals(2, second)
    }
}
