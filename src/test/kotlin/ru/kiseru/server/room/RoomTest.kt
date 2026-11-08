package ru.kiseru.server.room

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.kiseru.server.Server
import ru.kiseru.server.User

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
    fun `given room and user - when user added - then room becomes full`() {
        // given
        val room = Room()
        val user = mock<User>()

        // when
        room.addUser(user)

        // then
        assertTrue(room.isFull)
    }

    @Test
    fun `given room and user - when user added to empty room - then no join messages sent`() {
        // given
        val room = Room()
        val user = mock<User>()
        whenever(user.username).thenReturn("alice")

        // when
        room.addUser(user)

        // then
        verify(user, never()).sendMessage(anyString())
    }

    @Test
    fun `given full room - when another user added - then user is delegated to server not full room`() {
        // given
        val room = Room()
        val firstUser = mock<User>()
        whenever(firstUser.username).thenReturn("alice")
        val secondUser = mock<User>()
        whenever(secondUser.username).thenReturn("bob")
        room.addUser(firstUser)

        // when
        room.addUser(secondUser)

        // then
        verify(secondUser, never()).sendMessage(anyString())
        val notFullRoomBefore = Server.getNotFullRoom()
        assertNotEquals(room, notFullRoomBefore)
    }

    @Test
    fun `given room with user - when rating requested - then rating contains user row`() {
        // given
        val room = Room()
        val user = mock<User>()
        whenever(user.score).thenReturn(100)
        whenever(user.toString()).thenReturn("alice 100")
        room.addUser(user)

        // when
        val actual = room.rating

        // then
        assertEquals("alice 100\n", actual)
    }

    @Test
    fun `given room with users - when rating requested - then users sorted by score ascending`() {
        // given
        val room = Room()
        val firstUser = mock<User>()
        whenever(firstUser.score).thenReturn(200)
        whenever(firstUser.toString()).thenReturn("bob 200")
        val secondUser = mock<User>()
        whenever(secondUser.score).thenReturn(100)
        whenever(secondUser.toString()).thenReturn("alice 100")
        setUsers(room, arrayListOf(firstUser, secondUser))

        // when
        val actual = room.rating

        // then
        assertEquals("alice 100\nbob 200\n", actual)
    }

    @Test
    fun `given room with alive user - when alive count requested - then one returned`() {
        // given
        val room = Room()
        val user = mock<User>()
        whenever(user.getIsAlive()).thenReturn(true)
        room.addUser(user)

        // when
        val actual = room.aliveCount()

        // then
        assertEquals(1, actual)
    }

    @Test
    fun `given room with dead user - when alive count requested - then zero returned`() {
        // given
        val room = Room()
        val user = mock<User>()
        whenever(user.getIsAlive()).thenReturn(false)
        room.addUser(user)

        // when
        val actual = room.aliveCount()

        // then
        assertEquals(0, actual)
    }

    @Test
    fun `given room with user - when user removed - then room becomes not full`() {
        // given
        val room = Room()
        val user = mock<User>()
        room.addUser(user)

        // when
        room.removeUser(user)

        // then
        assertFalse(room.isFull)
    }

    @Test
    fun `given room with user - when user removed - then rating becomes empty`() {
        // given
        val room = Room()
        val user = mock<User>()
        whenever(user.toString()).thenReturn("alice 100")
        room.addUser(user)

        // when
        room.removeUser(user)

        // then
        assertEquals("", room.rating)
    }

    @Test
    fun `given room with user - when alive count requested after removal - then zero returned`() {
        // given
        val room = Room()
        val user = mock<User>()
        whenever(user.getIsAlive()).thenReturn(true)
        room.addUser(user)
        room.removeUser(user)

        // when
        val actual = room.aliveCount()

        // then
        assertEquals(0, actual)
    }

    @Test
    fun `given empty room - when user removed - then room stays not full`() {
        // given
        val room = Room()
        val user = mock<User>()

        // when
        room.removeUser(user)

        // then
        assertFalse(room.isFull)
    }

    @Test
    fun `given room with user - when another user removed - then room stays full`() {
        // given
        val room = Room()
        val user = mock<User>()
        val stranger = mock<User>()
        room.addUser(user)

        // when
        room.removeUser(stranger)

        // then
        assertTrue(room.isFull)
    }

    @Test
    fun `given room with user - when room run finishes - then game created and finish messages sent`() {
        // given
        val room = Room()
        val user = mock<User>()
        whenever(user.getIsAlive()).thenReturn(true)
        whenever(user.toString()).thenReturn("alice 100")
        room.addUser(user)
        room.start()
        waitUntilWaiting(room)

        // when
        synchronized(room) {
            (room as Object).notify()
        }
        room.join(5000)

        // then
        assertTrue(room.isGameFinished)
        assertNotNull(room.game)
        val captor = argumentCaptor<String>()
        verify(user, atLeastOnce()).sendMessage(captor.capture())
        assertTrue(captor.firstValue == "start")
        assertTrue(captor.allValues.contains("finish"))
        assertTrue(captor.allValues.contains("alice 100\n"))
    }

    @Test
    fun `given room with user - when room run starts gaming - then spaceship registered for user`() {
        // given
        val room = Room()
        val user = mock<User>()
        whenever(user.getIsAlive()).thenReturn(true)
        whenever(user.toString()).thenReturn("alice 100")
        room.addUser(user)
        room.start()
        waitUntilWaiting(room)

        // when
        synchronized(room) {
            (room as Object).notify()
        }
        room.join(5000)

        // then
        verify(user).setSpaceship(org.mockito.kotlin.any())
    }

    private fun setUsers(room: Room, users: ArrayList<User>) {
        val field = Room::class.java.getDeclaredField("users")
        field.isAccessible = true
        field.set(room, users)
    }

    private fun waitUntilWaiting(room: Room) {
        val deadline = System.currentTimeMillis() + 5000
        while (room.state != Thread.State.WAITING && System.currentTimeMillis() < deadline) {
            Thread.sleep(10)
        }
    }
}
