package ru.kiseru.server.room

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.kiseru.logics.User
import ru.kiseru.logics.models.Spaceship
import ru.kiseru.server.UserHandler

class RoomTest {

    @Test
    fun `given new room - when created - then game is not started and not finished`() {
        // given
        val room = Room()

        // when & then
        assertFalse(room.isGameStarted)
        assertFalse(room.isGameFinished)
    }

    @Test
    fun `given new room - when created - then room is not full`() {
        // given
        val room = Room()

        // when & then
        assertFalse(room.isFull)
    }

    @Test
    fun `given new room - when created - then alive count is zero`() {
        // given
        val room = Room()

        // when
        val actual = room.aliveCount()

        // then
        assertEquals(0, actual)
    }

    @Test
    fun `given new room - when created - then game is null`() {
        // given
        val room = Room()

        // when & then
        assertNull(room.game)
    }

    @Test
    fun `given new room - when rating requested - then empty string returned`() {
        // given
        val room = Room()

        // when
        val actual = room.rating

        // then
        assertEquals("", actual)
    }

    @Test
    fun `given room and userHandler - when userHandler added - then room becomes full`() {
        // given
        val room = Room()
        val userHandler = mock<UserHandler>()

        // when
        room.addUser(userHandler)

        // then
        assertTrue(room.isFull)
    }

    @Test
    fun `given not full room - when userHandler added - then same room returned`() {
        // given
        val room = Room()
        val userHandler = mock<UserHandler>()

        // when
        val actual = room.addUser(userHandler)

        // then
        assertSame(room, actual)
    }

    @Test
    fun `given room and userHandler - when userHandler added to empty room - then no join messages sent`() {
        // given
        val room = Room()
        val userHandler = mock<UserHandler>()

        // when
        room.addUser(userHandler)

        // then
        verify(userHandler, never()).sendMessage(anyString())
    }

    @Test
    fun `given full room - when another userHandler added - then userHandler is delegated to server not full room`() {
        // given
        val room = Room()
        val firstUserHandler = mock<UserHandler>()
        val secondUserHandler = mock<UserHandler>()
        whenever(secondUserHandler.score).thenReturn(100)
        whenever(secondUserHandler.toString()).thenReturn("bob 100")
        room.addUser(firstUserHandler)

        // when
        val actual = room.addUser(secondUserHandler)

        // then
        verify(secondUserHandler, never()).sendMessage(anyString())
        assertNotEquals(room, actual)
        assertTrue(actual.rating.contains("bob 100\n"))
    }

    @Test
    fun `given room with userHandler - when rating requested - then rating contains userHandler row`() {
        // given
        val room = Room()
        val userHandler = mock<UserHandler>()
        whenever(userHandler.score).thenReturn(100)
        whenever(userHandler.toString()).thenReturn("alice 100")
        room.addUser(userHandler)

        // when
        val actual = room.rating

        // then
        assertEquals("alice 100\n", actual)
    }

    @Test
    fun `given room with users - when rating requested - then users sorted by score ascending`() {
        // given
        val room = Room()
        val firstUserHandler = mock<UserHandler>()
        whenever(firstUserHandler.score).thenReturn(200)
        whenever(firstUserHandler.toString()).thenReturn("bob 200")
        val secondUserHandler = mock<UserHandler>()
        whenever(secondUserHandler.score).thenReturn(100)
        whenever(secondUserHandler.toString()).thenReturn("alice 100")
        setUserHandlers(room, arrayListOf(firstUserHandler, secondUserHandler))

        // when
        val actual = room.rating

        // then
        assertEquals("alice 100\nbob 200\n", actual)
    }

    @Test
    fun `given room with userHandler with alive spaceship - when alive count requested - then one returned`() {
        // given
        val room = Room()
        val userHandler = mockUserHandlerWithSpaceship(Spaceship.Status.ALIVE)
        room.addUser(userHandler)

        // when
        val actual = room.aliveCount()

        // then
        assertEquals(1, actual)
    }

    @Test
    fun `given room with userHandler with destroyed spaceship - when alive count requested - then zero returned`() {
        // given
        val room = Room()
        val userHandler = mockUserHandlerWithSpaceship(Spaceship.Status.DESTROYED)
        room.addUser(userHandler)

        // when
        val actual = room.aliveCount()

        // then
        assertEquals(0, actual)
    }

    @Test
    fun `given room with userHandler - when userHandler removed - then room becomes not full`() {
        // given
        val room = Room()
        val userHandler = mock<UserHandler>()
        room.addUser(userHandler)

        // when
        room.removeUser(userHandler)

        // then
        assertFalse(room.isFull)
    }

    @Test
    fun `given room with userHandler - when userHandler removed - then rating becomes empty`() {
        // given
        val room = Room()
        val userHandler = mock<UserHandler>()
        whenever(userHandler.toString()).thenReturn("alice 100")
        room.addUser(userHandler)

        // when
        room.removeUser(userHandler)

        // then
        assertEquals("", room.rating)
    }

    @Test
    fun `given room with userHandler - when alive count requested after removal - then zero returned`() {
        // given
        val room = Room()
        val userHandler = mockUserHandlerWithSpaceship(Spaceship.Status.ALIVE)
        room.addUser(userHandler)
        room.removeUser(userHandler)

        // when
        val actual = room.aliveCount()

        // then
        assertEquals(0, actual)
    }

    @Test
    fun `given empty room - when userHandler removed - then room stays not full`() {
        // given
        val room = Room()
        val userHandler = mock<UserHandler>()

        // when
        room.removeUser(userHandler)

        // then
        assertFalse(room.isFull)
    }

    @Test
    fun `given room with userHandler - when another userHandler removed - then room stays full`() {
        // given
        val room = Room()
        val userHandler = mock<UserHandler>()
        val stranger = mock<UserHandler>()
        room.addUser(userHandler)

        // when
        room.removeUser(stranger)

        // then
        assertTrue(room.isFull)
    }

    @Test
    fun `given room with userHandler - when room run finishes - then game created and finish messages sent`() {
        // given
        val room = Room()
        val userHandler = mock<UserHandler>()
        val spaceship = mock<Spaceship>()
        whenever(spaceship.status).thenReturn(Spaceship.Status.ALIVE)
        whenever(userHandler.spaceship).thenReturn(spaceship)
        whenever(userHandler.user).thenReturn(User(1, "alice"))
        whenever(userHandler.toString()).thenReturn("alice 100")
        room.addUser(userHandler)
        val thread = Thread(room::run)
        thread.start()
        waitUntilWaiting(thread)

        // when
        whenever(spaceship.status).thenReturn(Spaceship.Status.DESTROYED)
        room.notifyIfNoAliveUsers()
        thread.join(5000)

        // then
        assertTrue(room.isGameFinished)
        assertNotNull(room.game)
        val captor = argumentCaptor<String>()
        verify(userHandler, atLeastOnce()).sendMessage(captor.capture())
        assertTrue(captor.firstValue == "start")
        assertTrue(captor.allValues.contains("finish"))
        assertTrue(captor.allValues.contains("alice 100\n"))
    }

    @Test
    fun `given room with userHandler - when room run starts gaming - then spaceship registered for userHandler`() {
        // given
        val room = Room()
        val userHandler = mock<UserHandler>()
        val spaceship = mock<Spaceship>()
        whenever(spaceship.status).thenReturn(Spaceship.Status.ALIVE)
        whenever(userHandler.spaceship).thenReturn(spaceship)
        whenever(userHandler.user).thenReturn(User(1, "alice"))
        whenever(userHandler.toString()).thenReturn("alice 100")
        room.addUser(userHandler)
        val thread = Thread(room::run)
        thread.start()
        waitUntilWaiting(thread)

        // when
        whenever(spaceship.status).thenReturn(Spaceship.Status.DESTROYED)
        room.notifyIfNoAliveUsers()
        thread.join(5000)

        // then
        verify(userHandler).setSpaceship(org.mockito.kotlin.any())
    }

    @Test
    fun `given room with userHandler who left before game start - when room starts - then room finishes without waiting`() {
        // given
        val room = Room()
        val userHandler = mockUserHandlerWithSpaceship(Spaceship.Status.DESTROYED)
        whenever(userHandler.user).thenReturn(User(1, "alice"))
        whenever(userHandler.toString()).thenReturn("alice 100")
        room.addUser(userHandler)

        // when
        val thread = Thread(room::run)
        thread.start()
        thread.join(5000)

        // then
        assertFalse(thread.isAlive)
        assertTrue(room.isGameFinished)
    }

    @Test
    fun `given room with userHandler with alive spaceship - when room starts - then room waits for the spaceship to be destroyed`() {
        // given
        val room = Room()
        val userHandler = mockUserHandlerWithSpaceship(Spaceship.Status.ALIVE)
        whenever(userHandler.user).thenReturn(User(1, "alice"))
        whenever(userHandler.toString()).thenReturn("alice 100")
        room.addUser(userHandler)

        // when
        val thread = Thread(room::run)
        thread.start()
        waitUntilWaiting(thread)

        // then
        assertTrue(thread.isAlive)
        assertFalse(room.isGameFinished)
        thread.interrupt()
        thread.join(5000)
    }

    private fun mockUserHandlerWithSpaceship(status: Spaceship.Status): UserHandler {
        val userHandler = mock<UserHandler>()
        val spaceship = mock<Spaceship>()
        whenever(spaceship.status).thenReturn(status)
        whenever(userHandler.spaceship).thenReturn(spaceship)
        return userHandler
    }

    private fun setUserHandlers(room: Room, userHandlers: ArrayList<UserHandler>) {
        val field = Room::class.java.getDeclaredField("userHandlers")
        field.isAccessible = true
        field.set(room, userHandlers)
    }

    private fun waitUntilWaiting(thread: Thread) {
        val deadline = System.currentTimeMillis() + 5000
        while (thread.state != Thread.State.WAITING && System.currentTimeMillis() < deadline) {
            Thread.sleep(10)
        }
    }
}
