package ru.kiseru.server

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.kiseru.logics.CourseChecker
import ru.kiseru.logics.Game
import ru.kiseru.logics.auxiliary.Direction
import ru.kiseru.logics.models.Spaceship
import ru.kiseru.server.room.Room
import java.io.BufferedReader
import java.io.PrintWriter

class UserTest {

    @Test
    fun `given new user - when created - then score is 100`() {
        // given
        val user = createUser()

        // when & then
        assertEquals(100, user.score)
    }

    @Test
    fun `given new user - when created - then user is alive`() {
        // given
        val user = createUser()

        // when & then
        assertTrue(user.getIsAlive())
    }

    @Test
    fun `given new user - when created - then username is null`() {
        // given
        val user = createUser()

        // when & then
        assertNull(user.username)
    }

    @Test
    fun `given user and room - when room requested - then same room returned`() {
        // given
        val room = mock<Room>()
        val user = createUser(room = room)

        // when
        val actual = user.room

        // then
        assertSame(room, actual)
    }

    @Test
    fun `given user - when message sent - then writer receives message`() {
        // given
        val writer = mock<PrintWriter>()
        val user = createUser(writer = writer)

        // when
        user.sendMessage("hello")

        // then
        verify(writer).println("hello")
    }

    @Test
    fun `given user in started room - when score added - then score increased by 10`() {
        // given
        val room = mock<Room>()
        whenever(room.isGameStarted).thenReturn(true)
        val user = createUser(room = room)

        // when
        user.addScore()

        // then
        assertEquals(110, user.score)
    }

    @Test
    fun `given user in not started room - when score added - then score unchanged`() {
        // given
        val room = mock<Room>()
        whenever(room.isGameStarted).thenReturn(false)
        val user = createUser(room = room)

        // when
        user.addScore()

        // then
        assertEquals(100, user.score)
    }

    @Test
    fun `given user in started room - when score subtracted once - then score decreased by 50 and user alive`() {
        // given
        val room = mock<Room>()
        whenever(room.isGameStarted).thenReturn(true)
        val user = createUser(room = room)

        // when
        user.subtractScore()

        // then
        assertEquals(50, user.score)
        assertTrue(user.getIsAlive())
    }

    @Test
    fun `given user in started room - when score subtracted below zero - then user dies`() {
        // given
        val room = mock<Room>()
        whenever(room.isGameStarted).thenReturn(true)
        val user = createUser(room = room)

        // when
        user.subtractScore()
        user.subtractScore()
        user.subtractScore()

        // then
        assertEquals(-50, user.score)
        assertFalse(user.getIsAlive())
    }

    @Test
    fun `given user in not started room - when score subtracted - then score unchanged and user alive`() {
        // given
        val room = mock<Room>()
        whenever(room.isGameStarted).thenReturn(false)
        val user = createUser(room = room)

        // when
        user.subtractScore()

        // then
        assertEquals(100, user.score)
        assertTrue(user.getIsAlive())
    }

    @Test
    fun `given alive user - when died - then user is not alive and death messages sent`() {
        // given
        val writer = mock<PrintWriter>()
        val user = createUser(writer = writer)

        // when
        user.died()

        // then
        assertFalse(user.getIsAlive())
        verify(writer).println("died")
        verify(writer).println("You have collected 100 score")
    }

    @Test
    fun `given user with username - when converted to string - then username and score returned`() {
        // given
        val user = createUser()
        setUsername(user, "alice")

        // when
        val actual = user.toString()

        // then
        assertEquals("alice 100", actual)
    }

    @Test
    fun `given user and not full room - when run - then user added to room and intro messages sent`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val spaceship = mock<Spaceship>()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("alice", "unknown")
        val user = User(reader, writer, room)
        user.setSpaceship(spaceship)

        // when
        runUserUntilFinished(user)

        // then
        verify(room).addUser(user)
        verify(writer).println("Welcome To Asteroids Server")
        verify(writer).println("Please, introduce yourself!")
        verify(writer).println("Good luck, Commander!")
        verify(writer).println("Unknown command")
        verify(spaceship).setDirection(Direction.UP)
        assertEquals("alice", user.username)
    }

    @Test
    fun `given user and full room - when run - then room started`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val spaceship = mock<Spaceship>()
        whenever(room.isFull).thenReturn(true)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("alice", "unknown")
        val user = User(reader, writer, room)
        user.setSpaceship(spaceship)

        // when
        runUserUntilFinished(user)

        // then
        verify(room).start()
    }

    @Test
    fun `given user in game - when go command received - then spaceship moves and score sent`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val game = mock<Game>()
        val spaceship = mock<Spaceship>()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(room.getGame()).thenReturn(game)
        whenever(reader.readLine()).thenReturn("alice", "go")
        val user = User(reader, writer, room)
        user.setSpaceship(spaceship)

        // when
        runUserUntilFinished(user)

        // then
        verify(spaceship).go()
        verify(game).refresh()
        verify(writer).println("100")
    }

    @ParameterizedTest
    @CsvSource("left,LEFT", "right,RIGHT", "up,UP", "down,DOWN")
    fun `given user in game - when direction command received - then spaceship direction changed and success sent`(
        command: String,
        direction: Direction,
    ) {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val game = mock<Game>()
        val spaceship = mock<Spaceship>()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(room.getGame()).thenReturn(game)
        whenever(reader.readLine()).thenReturn("alice", command)
        val user = User(reader, writer, room)
        user.setSpaceship(spaceship)

        // when
        runUserUntilFinished(user)

        // then
        verify(spaceship, atLeastOnce()).setDirection(direction)
        verify(game).refresh()
        verify(writer).println("success")
    }

    @Test
    fun `given user in game - when isAsteroid command received and asteroid ahead - then t sent`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val spaceship = mock<Spaceship>()
        val courseChecker = mock<CourseChecker>()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(spaceship.courseChecker).thenReturn(courseChecker)
        whenever(courseChecker.isAsteroid()).thenReturn(true)
        whenever(reader.readLine()).thenReturn("alice", "isAsteroid")
        val user = User(reader, writer, room)
        user.setSpaceship(spaceship)

        // when
        runUserUntilFinished(user)

        // then
        verify(writer).println("t")
    }

    @Test
    fun `given user in game - when isGarbage command received and no garbage ahead - then f sent`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val spaceship = mock<Spaceship>()
        val courseChecker = mock<CourseChecker>()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(spaceship.courseChecker).thenReturn(courseChecker)
        whenever(courseChecker.isGarbage()).thenReturn(false)
        whenever(reader.readLine()).thenReturn("alice", "isGarbage")
        val user = User(reader, writer, room)
        user.setSpaceship(spaceship)

        // when
        runUserUntilFinished(user)

        // then
        verify(writer).println("f")
    }

    @Test
    fun `given user in game - when isWall command received and wall ahead - then t sent`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val spaceship = mock<Spaceship>()
        val courseChecker = mock<CourseChecker>()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(spaceship.courseChecker).thenReturn(courseChecker)
        whenever(courseChecker.isWall()).thenReturn(true)
        whenever(reader.readLine()).thenReturn("alice", "isWall")
        val user = User(reader, writer, room)
        user.setSpaceship(spaceship)

        // when
        runUserUntilFinished(user)

        // then
        verify(writer).println("t")
    }

    @Test
    fun `given user in game - when 1500 steps performed - then user died`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val game = mock<Game>()
        val spaceship = mock<Spaceship>()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false)
        whenever(room.getGame()).thenReturn(game)
        whenever(reader.readLine()).thenAnswer { "go" }
        val user = User(reader, writer, room)
        user.setSpaceship(spaceship)

        // when
        runUserUntilFinished(user)

        // then
        assertFalse(user.getIsAlive())
        verify(writer).println("died")
        verify(writer).println("You have collected 100 score")
    }

    private fun createUser(
        reader: BufferedReader = mock(),
        writer: PrintWriter = mock(),
        room: Room = mock(),
    ): User = User(reader, writer, room)

    private fun runUserUntilFinished(user: User) {
        user.start()
        waitUntilWaiting(user)
        user.interrupt()
        user.join(5000)
    }

    private fun waitUntilWaiting(user: User) {
        val deadline = System.currentTimeMillis() + 5000
        while (user.state != Thread.State.WAITING && System.currentTimeMillis() < deadline) {
            Thread.sleep(10)
        }
    }

    private fun setUsername(user: User, username: String) {
        val field = User::class.java.getDeclaredField("username")
        field.isAccessible = true
        field.set(user, username)
    }
}
