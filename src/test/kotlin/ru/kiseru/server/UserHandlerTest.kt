package ru.kiseru.server

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.kiseru.logics.CourseChecker
import ru.kiseru.logics.Game
import ru.kiseru.logics.User
import ru.kiseru.logics.auxiliary.Direction
import ru.kiseru.logics.models.Spaceship
import ru.kiseru.server.room.Room
import java.io.BufferedReader
import java.io.PrintWriter

class UserHandlerTest {

    @Test
    fun `given new userHandler - when created - then score is 100`() {
        // given
        val userHandler = createUserHandler()

        // when & then
        assertEquals(100, userHandler.score)
    }

    @Test
    fun `given new userHandler - when created - then userHandler is alive`() {
        // given
        val userHandler = createUserHandler()

        // when & then
        assertTrue(userHandler.isAlive)
    }

    @Test
    fun `given userHandler and room - when room requested - then same room returned`() {
        // given
        val room = mock<Room>()
        val userHandler = createUserHandler(room = room)

        // when
        val actual = userHandler.room

        // then
        assertSame(room, actual)
    }

    @Test
    fun `given userHandler - when message sent - then writer receives message`() {
        // given
        val writer = mock<PrintWriter>()
        val userHandler = createUserHandler(writer = writer)

        // when
        userHandler.sendMessage("hello")

        // then
        verify(writer).println("hello")
    }

    @Test
    fun `given userHandler in started room - when score added - then score increased by 10`() {
        // given
        val room = mock<Room>()
        whenever(room.isGameStarted).thenReturn(true)
        val userHandler = createUserHandler(room = room)

        // when
        userHandler.addScore()

        // then
        assertEquals(110, userHandler.score)
    }

    @Test
    fun `given userHandler in not started room - when score added - then score unchanged`() {
        // given
        val room = mock<Room>()
        whenever(room.isGameStarted).thenReturn(false)
        val userHandler = createUserHandler(room = room)

        // when
        userHandler.addScore()

        // then
        assertEquals(100, userHandler.score)
    }

    @Test
    fun `given userHandler in started room - when score subtracted once - then score decreased by 50 and userHandler alive`() {
        // given
        val room = mock<Room>()
        whenever(room.isGameStarted).thenReturn(true)
        val userHandler = createUserHandler(room = room)

        // when
        userHandler.subtractScore()

        // then
        assertEquals(50, userHandler.score)
        assertTrue(userHandler.isAlive)
    }

    @Test
    fun `given userHandler in started room - when score subtracted below zero - then userHandler dies`() {
        // given
        val room = mock<Room>()
        whenever(room.isGameStarted).thenReturn(true)
        val userHandler = createUserHandler(room = room)

        // when
        userHandler.subtractScore()
        userHandler.subtractScore()
        userHandler.subtractScore()

        // then
        assertEquals(-50, userHandler.score)
        assertFalse(userHandler.isAlive)
    }

    @Test
    fun `given userHandler in not started room - when score subtracted - then score unchanged and userHandler alive`() {
        // given
        val room = mock<Room>()
        whenever(room.isGameStarted).thenReturn(false)
        val userHandler = createUserHandler(room = room)

        // when
        userHandler.subtractScore()

        // then
        assertEquals(100, userHandler.score)
        assertTrue(userHandler.isAlive)
    }

    @Test
    fun `given alive userHandler - when died - then userHandler is not alive and death messages sent`() {
        // given
        val writer = mock<PrintWriter>()
        val userHandler = createUserHandler(writer = writer)

        // when
        userHandler.died()

        // then
        assertFalse(userHandler.isAlive)
        verify(writer).println("died")
        verify(writer).println("You have collected 100 score")
    }

    @Test
    fun `given userHandler and not full room - when run - then userHandler added to room and intro messages sent`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val spaceship = mock<Spaceship>()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("alice", "unknown")
        val userHandler = createUserHandler(reader, writer, room)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        verify(room).addUser(userHandler)
        verify(writer).println("Welcome To Asteroids Server")
        verify(writer).println("Please, introduce yourself!")
        verify(writer).println("Good luck, Commander!")
        verify(writer).println("Unknown command")
        verify(spaceship).direction = Direction.UP
    }

    @Test
    fun `given userHandler and username - when run - then user filled`() {
        // given
        val reader = mock<BufferedReader>()
        val room = mock<Room>()
        val spaceship = mock<Spaceship>()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("alice", "unknown")
        val userHandler = createUserHandler(reader, room = room)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        assertEquals(User(0, "alice"), userHandler.user)
    }

    @Test
    fun `given room that delegates userHandler to another room - when run - then userHandler room updated`() {
        // given
        val reader = mock<BufferedReader>()
        val initialRoom = mock<Room>()
        val actualRoom = mock<Room>()
        val spaceship = mock<Spaceship>()
        whenever(initialRoom.addUser(any())).thenReturn(actualRoom)
        whenever(actualRoom.isFull).thenReturn(false)
        whenever(actualRoom.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("alice", "unknown")
        val userHandler = UserHandler(0, reader, mock(), initialRoom)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        assertSame(actualRoom, userHandler.room)
    }

    @Test
    fun `given userHandler and full room - when run - then room started`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val spaceship = mock<Spaceship>()
        whenever(room.isFull).thenReturn(true)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("alice", "unknown")
        val userHandler = createUserHandler(reader, writer, room)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        verify(room, timeout(5000)).run()
    }

    @Test
    fun `given userHandler - when run finishes - then room notified that no alive users left`() {
        // given
        val reader = mock<BufferedReader>()
        val room = mock<Room>()
        val spaceship = mock<Spaceship>()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("alice", "unknown")
        val userHandler = createUserHandler(reader, mock(), room)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        verify(room).notifyIfNoAliveUsers()
    }

    @Test
    fun `given userHandler in game - when go command received - then spaceship moves and score sent`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val game = mock<Game>()
        val spaceship = mock<Spaceship>()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(room.game).thenReturn(game)
        whenever(reader.readLine()).thenReturn("alice", "go")
        val userHandler = createUserHandler(reader, writer, room)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        verify(spaceship).go()
        verify(game).refresh()
        verify(writer).println("100")
    }

    @ParameterizedTest
    @CsvSource("left,LEFT", "right,RIGHT", "up,UP", "down,DOWN")
    fun `given userHandler in game - when direction command received - then spaceship direction changed and success sent`(
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
        whenever(room.game).thenReturn(game)
        whenever(reader.readLine()).thenReturn("alice", command)
        val userHandler = createUserHandler(reader, writer, room)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        verify(spaceship, atLeastOnce()).direction = direction
        verify(game).refresh()
        verify(writer).println("success")
    }

    @Test
    fun `given userHandler in game - when isAsteroid command received and asteroid ahead - then t sent`() {
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
        val userHandler = createUserHandler(reader, writer, room)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        verify(writer).println("t")
    }

    @Test
    fun `given userHandler in game - when isGarbage command received and no garbage ahead - then f sent`() {
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
        val userHandler = createUserHandler(reader, writer, room)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        verify(writer).println("f")
    }

    @Test
    fun `given userHandler in game - when isWall command received and wall ahead - then t sent`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val spaceship = mock<Spaceship>()
        val courseChecker = mock<CourseChecker>()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(spaceship.courseChecker).thenReturn(courseChecker)
        whenever(courseChecker.isWall).thenReturn(true)
        whenever(reader.readLine()).thenReturn("alice", "isWall")
        val userHandler = createUserHandler(reader, writer, room)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        verify(writer).println("t")
    }

    @Test
    fun `given userHandler in game - when 1500 steps performed - then userHandler died`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val game = mock<Game>()
        val spaceship = mock<Spaceship>()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false)
        whenever(room.game).thenReturn(game)
        whenever(reader.readLine()).thenAnswer { "go" }
        val userHandler = createUserHandler(reader, writer, room)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        assertFalse(userHandler.isAlive)
        verify(writer).println("died")
        verify(writer).println("You have collected 100 score")
    }

    private fun createUserHandler(
        reader: BufferedReader = mock(),
        writer: PrintWriter = mock(),
        room: Room = mock(),
    ): UserHandler {
        whenever(room.addUser(any())).thenReturn(room)
        return UserHandler(0, reader, writer, room)
    }

    private fun runUserHandlerUntilFinished(userHandler: UserHandler) {
        val thread = Thread { userHandler.run() }
        thread.start()
        thread.join(5000)
    }
}
