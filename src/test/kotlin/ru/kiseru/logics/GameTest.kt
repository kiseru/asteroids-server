package ru.kiseru.logics

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.kiseru.logics.auxiliary.Direction
import ru.kiseru.logics.auxiliary.Position
import ru.kiseru.logics.auxiliary.ScreenSize
import ru.kiseru.logics.auxiliary.Type
import ru.kiseru.logics.handlers.SpaceshipCrashHandler
import ru.kiseru.logics.models.Asteroid
import ru.kiseru.logics.models.Garbage
import ru.kiseru.logics.models.Spaceship
import ru.kiseru.server.UserHandler

class GameTest {

    @Test
    fun `given screen and counts - when game created - then garbage and asteroids generated`() {
        // given
        val screen = Screen(ScreenSize(10, 10))

        // when
        val game = Game(screen, 3, 2)

        // then
        assertEquals(3, game.pointsOnScreen.count { it.type == Type.GARBAGE })
        assertEquals(2, game.pointsOnScreen.count { it.type == Type.ASTEROID })
    }

    @Test
    fun `given screen and counts - when game created - then all coordinates unique and within screen bounds`() {
        // given
        val screen = Screen(ScreenSize(10, 10))

        // when
        val game = Game(screen, 4, 3)

        // then
        val coordinates = game.pointsOnScreen.map { it.position }
        assertEquals(coordinates.size, coordinates.distinct().size)
        assertTrue(coordinates.all { it.x() in 1..screen.size.width() && it.y() in 1..screen.size.height() })
    }

    @Test
    fun `given screen and counts - when game created - then garbage number stored`() {
        // given
        val screen = Screen(ScreenSize(10, 10))

        // when
        val game = Game(screen, 7, 1)

        // then
        assertEquals(7, game.garbageNumber)
    }

    @Test
    fun `given screen - when game created without garbage and asteroids - then points list is empty`() {
        // given
        val screen = Screen(ScreenSize(10, 10))

        // when
        val game = Game(screen, 0, 0)

        // then
        assertTrue(game.pointsOnScreen.isEmpty())
    }

    @Test
    fun `given screen - when game created - then screen stored`() {
        // given
        val screen = Screen(ScreenSize(10, 10))

        // when
        val game = Game(screen, 1, 1)

        // then
        assertSame(screen, game.screen)
    }

    @Test
    fun `given userHandler - when spaceship registered - then spaceship passed to userHandler`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 1, 1)
        val userHandler = mock<UserHandler>()
        val userId = UserId(1)

        // when
        game.registerSpaceshipForUser(userHandler, userId)

        // then
        val captor = argumentCaptor<Spaceship>()
        verify(userHandler).setSpaceship(captor.capture())
        val spaceship = captor.firstValue
        assertEquals(Type.SPACESHIP, spaceship.type)
    }

    @Test
    fun `given userHandler - when spaceship registered - then spaceship added to points on screen`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 2, 2)
        val userHandler = mock<UserHandler>()
        val userId = UserId(1)

        // when
        game.registerSpaceshipForUser(userHandler, userId)

        // then
        val captor = argumentCaptor<Spaceship>()
        verify(userHandler).setSpaceship(captor.capture())
        assertTrue(game.pointsOnScreen.contains(captor.firstValue))
    }

    @Test
    fun `given userHandler - when spaceship registered - then crash handler created`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 1, 1)
        val userHandler = mock<UserHandler>()
        val userId = UserId(1)

        // when
        game.registerSpaceshipForUser(userHandler, userId)

        // then
        assertEquals(1, game.crashHandlers.size)
    }

    @Test
    fun `given userHandler - when spaceship registered - then spaceship not collide with other points`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 4, 4)
        val userHandler = mock<UserHandler>()
        val userId = UserId(1)

        // when
        game.registerSpaceshipForUser(userHandler, userId)

        // then
        val captor = argumentCaptor<Spaceship>()
        verify(userHandler).setSpaceship(captor.capture())
        val spaceshipCoordinates = captor.firstValue.position
        val otherCoordinates = game.pointsOnScreen
            .filter { it.type != Type.SPACESHIP }
            .map { it.position }
        assertFalse(otherCoordinates.contains(spaceshipCoordinates))
    }

    @Test
    fun `given game - when refresh called concurrently - then refresh executions do not overlap`() {
        // given
        val activeRefreshes = AtomicInteger()
        val overlapDetected = AtomicBoolean(false)
        val screen = object : Screen(ScreenSize(10, 10)) {
            override fun update() {
                if (activeRefreshes.incrementAndGet() > 1) overlapDetected.set(true)
                Thread.sleep(50)
                activeRefreshes.decrementAndGet()
            }
        }
        val game = Game(screen, 0, 0)
        val start = CountDownLatch(1)
        val threads = (1..4).map {
            Thread {
                start.await()
                game.refresh()
            }
        }
        threads.forEach(Thread::start)

        // when
        start.countDown()
        threads.forEach { it.join(5000) }

        // then
        assertFalse(overlapDetected.get())
    }

    @Test
    fun `given game - when refresh called - then screen updated`() {
        // given
        val screen = mock<Screen>()
        whenever(screen.size).thenReturn(ScreenSize(10, 10))
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
        whenever(screen.size).thenReturn(ScreenSize(10, 10))
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
        val screen = Screen(ScreenSize(10, 10))
        val game = Game(screen, 1, 1)
        val garbage = game.pointsOnScreen.filterIsInstance<Garbage>().first()
        val asteroid = game.pointsOnScreen.filterIsInstance<Asteroid>().first()

        // when
        game.refresh()

        // then
        val lines = screen.display().split("\n")
        assertEquals("G", lines[garbage.y - 1].split("\t")[garbage.x - 1])
        assertEquals("A", lines[asteroid.y - 1].split("\t")[asteroid.x - 1])
    }

    @Test
    fun `given alive spaceship - when isAnyoneAlive called - then true returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 1, 1)
        val userHandler = mock<UserHandler>()
        val userId = UserId(1)
        game.registerSpaceshipForUser(userHandler, userId)

        // when
        val actual = game.isAnyoneAlive()

        // then
        assertTrue(actual)
    }

    @Test
    fun `given destroyed spaceship - when isAnyoneAlive called - then false returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 1, 1)
        val userHandler = mock<UserHandler>()
        val userId = UserId(1)
        game.registerSpaceshipForUser(userHandler, userId)
        game.pointsOnScreen.filterIsInstance<Spaceship>().first().destroy()

        // when
        val actual = game.isAnyoneAlive()

        // then
        assertFalse(actual)
    }

    @Test
    fun `given two spaceships one destroyed - when isAnyoneAlive called - then true returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 1, 1)
        game.registerSpaceshipForUser(mock<UserHandler>(), UserId(1))
        game.registerSpaceshipForUser(mock<UserHandler>(), UserId(2))
        game.pointsOnScreen.filterIsInstance<Spaceship>().first().destroy()

        // when
        val actual = game.isAnyoneAlive()

        // then
        assertTrue(actual)
    }

    @Test
    fun `given game without spaceships - when isAnyoneAlive called - then false returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 1, 1)

        // when & then
        assertFalse(game::isAnyoneAlive)
    }

    @Test
    fun `given game - when increment called first time - then one returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 1, 1)

        // when
        val actual = game.incrementCollectedGarbageCount()

        // then
        assertEquals(1, actual)
    }

    @Test
    fun `given game - when increment called twice - then counters incremented sequentially`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 1, 1)

        // when
        val first = game.incrementCollectedGarbageCount()
        val second = game.incrementCollectedGarbageCount()

        // then
        assertEquals(1, first)
        assertEquals(2, second)
    }

    @Test
    fun `given asteroid directly ahead of spaceship - when isAsteroidAheadOf called - then true returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(5, 5), UserId(1)).apply { direction = Direction.RIGHT }
        game.pointsOnScreen.add(Asteroid(Position(6, 5)))

        // when
        val actual = game.isAsteroidAheadOf(spaceship)

        // then
        assertTrue(actual)
    }

    @Test
    fun `given asteroid behind spaceship - when isAsteroidAheadOf called - then false returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(5, 5), UserId(1)).apply { direction = Direction.RIGHT }
        game.pointsOnScreen.add(Asteroid(Position(4, 5)))

        // when
        val actual = game.isAsteroidAheadOf(spaceship)

        // then
        assertFalse(actual)
    }

    @Test
    fun `given asteroid ahead in vertical direction - when isAsteroidAheadOf called - then true returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(5, 5), UserId(1)).apply { direction = Direction.DOWN }
        game.pointsOnScreen.add(Asteroid(Position(5, 6)))

        // when
        val actual = game.isAsteroidAheadOf(spaceship)

        // then
        assertTrue(actual)
    }

    @Test
    fun `given only garbage ahead of spaceship - when isAsteroidAheadOf called - then false returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(5, 5), UserId(1)).apply { direction = Direction.RIGHT }
        game.pointsOnScreen.add(Garbage(Position(6, 5)))

        // when
        val actual = game.isAsteroidAheadOf(spaceship)

        // then
        assertFalse(actual)
    }

    @Test
    fun `given garbage directly ahead of spaceship - when isGarbageAheadOf called - then true returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(5, 5), UserId(1)).apply { direction = Direction.LEFT }
        game.pointsOnScreen.add(Garbage(Position(4, 5)))

        // when
        val actual = game.isGarbageAheadOf(spaceship)

        // then
        assertTrue(actual)
    }

    @Test
    fun `given garbage behind spaceship - when isGarbageAheadOf called - then false returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(5, 5), UserId(1)).apply { direction = Direction.LEFT }
        game.pointsOnScreen.add(Garbage(Position(6, 5)))

        // when
        val actual = game.isGarbageAheadOf(spaceship)

        // then
        assertFalse(actual)
    }

    @Test
    fun `given garbage ahead in vertical direction - when isGarbageAheadOf called - then true returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(5, 5), UserId(1)).apply { direction = Direction.UP }
        game.pointsOnScreen.add(Garbage(Position(5, 4)))

        // when
        val actual = game.isGarbageAheadOf(spaceship)

        // then
        assertTrue(actual)
    }

    @Test
    fun `given only asteroid ahead of spaceship - when isGarbageAheadOf called - then false returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(5, 5), UserId(1)).apply { direction = Direction.LEFT }
        game.pointsOnScreen.add(Asteroid(Position(4, 5)))

        // when
        val actual = game.isGarbageAheadOf(spaceship)

        // then
        assertFalse(actual)
    }

    @Test
    fun `given spaceship at top edge facing up - when isWallAheadOf called - then true returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(5, 1), UserId(1)).apply { direction = Direction.UP }

        // when
        val actual = game.isWallAheadOf(spaceship)

        // then
        assertTrue(actual)
    }

    @Test
    fun `given spaceship not at top edge facing up - when isWallAheadOf called - then false returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(5, 2), UserId(1)).apply { direction = Direction.UP }

        // when
        val actual = game.isWallAheadOf(spaceship)

        // then
        assertFalse(actual)
    }

    @Test
    fun `given spaceship at right edge facing right - when isWallAheadOf called - then true returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(10, 5), UserId(1)).apply { direction = Direction.RIGHT }

        // when
        val actual = game.isWallAheadOf(spaceship)

        // then
        assertTrue(actual)
    }

    @Test
    fun `given spaceship not at right edge facing right - when isWallAheadOf called - then false returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(9, 5), UserId(1)).apply { direction = Direction.RIGHT }

        // when
        val actual = game.isWallAheadOf(spaceship)

        // then
        assertFalse(actual)
    }

    @Test
    fun `given spaceship at bottom edge facing down - when isWallAheadOf called - then true returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(5, 10), UserId(1)).apply { direction = Direction.DOWN }

        // when
        val actual = game.isWallAheadOf(spaceship)

        // then
        assertTrue(actual)
    }

    @Test
    fun `given spaceship not at bottom edge facing down - when isWallAheadOf called - then false returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(5, 9), UserId(1)).apply { direction = Direction.DOWN }

        // when
        val actual = game.isWallAheadOf(spaceship)

        // then
        assertFalse(actual)
    }

    @Test
    fun `given spaceship at left edge facing left - when isWallAheadOf called - then true returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(1, 5), UserId(1)).apply { direction = Direction.LEFT }

        // when
        val actual = game.isWallAheadOf(spaceship)

        // then
        assertTrue(actual)
    }

    @Test
    fun `given spaceship not at left edge facing left - when isWallAheadOf called - then false returned`() {
        // given
        val game = Game(Screen(ScreenSize(10, 10)), 0, 0)
        val spaceship = Spaceship(Position(2, 5), UserId(1)).apply { direction = Direction.LEFT }

        // when
        val actual = game.isWallAheadOf(spaceship)

        // then
        assertFalse(actual)
    }
}
