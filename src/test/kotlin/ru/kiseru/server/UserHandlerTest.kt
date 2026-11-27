package ru.kiseru.server

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.kiseru.logics.Game
import ru.kiseru.logics.Score
import ru.kiseru.logics.Steps
import ru.kiseru.logics.User
import ru.kiseru.logics.UserId
import ru.kiseru.logics.auxiliary.Direction
import ru.kiseru.logics.auxiliary.Position
import ru.kiseru.logics.auxiliary.Type
import ru.kiseru.logics.models.Spaceship
import ru.kiseru.server.room.Room
import java.io.BufferedReader
import java.io.PrintWriter

class UserHandlerTest {

    @Test
    fun `given userHandler with spaceship - when score requested - then spaceship score returned`() {
        // given
        val userHandler = createUserHandler()

        val userId = UserId(1)
        val spaceship = Spaceship(Position(1, 1), userId)
        spaceship.score = Score(100)
        userHandler.setSpaceship(spaceship)

        // when
        val actual = userHandler.score

        // then
        assertEquals(100, actual)
    }

    @Test
    fun `given userHandler without spaceship - when score requested - then zero returned`() {
        // given
        val userHandler = createUserHandler()

        // when
        val actual = userHandler.score

        // then
        assertEquals(0, actual)
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
    fun `given alive spaceship - when died - then spaceship destroyed and death messages sent`() {
        // given
        val writer = mock<PrintWriter>()
        val userHandler = createUserHandler(writer = writer)
        val spaceship = mockSpaceship()
        userHandler.setSpaceship(spaceship)

        // when
        userHandler.died()

        // then
        verify(spaceship).destroy()
        verify(writer).println("died")
        verify(writer).println("You have collected 100 score")
    }

    @Test
    fun `given userHandler and not full room - when run - then userHandler added to room and messages sent`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val spaceship = mockSpaceship()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("unknown")
        val userHandler = createUserHandler(reader, writer, room)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        verify(room).addUser(userHandler)
        verify(writer).println("You need to keep a space garbage.")
        verify(writer).println("Your ID is 1")
        verify(writer).println("Good luck, Commander!")
        verify(writer).println("Unknown command")
        verify(spaceship).direction = Direction.UP
    }

    @Test
    fun `given user - when userHandler created - then same user returned`() {
        // given
        val userId = UserId(7)
        val user = User(userId, "alice")

        // when
        val userHandler = createUserHandler(user = user)

        // then
        assertSame(user, userHandler.user)
    }

    @Test
    fun `given room that delegates userHandler to another room - when run - then userHandler room updated`() {
        // given
        val reader = mock<BufferedReader>()
        val initialRoom = mock<Room>()
        val actualRoom = mock<Room>()
        val spaceship = mockSpaceship()
        whenever(initialRoom.addUser(any())).thenReturn(actualRoom)
        whenever(actualRoom.isFull).thenReturn(false)
        whenever(actualRoom.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("unknown")
        val userHandler = UserHandler(reader, mock(), initialRoom, User(UserId(1), "alice"))
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
        val spaceship = mockSpaceship()
        whenever(room.isFull).thenReturn(true)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("unknown")
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
        val spaceship = mockSpaceship()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("unknown")
        val userHandler = createUserHandler(reader, mock(), room)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        verify(spaceship).destroy()
        verify(room).notifyIfNoAliveUsers()
    }

    @Test
    fun `given userHandler in game - when go command received - then spaceship moves and score sent`() {
        // given
        val reader = mock<BufferedReader>()
        val writer = mock<PrintWriter>()
        val room = mock<Room>()
        val game = mock<Game>()
        val spaceship = mockSpaceship()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(room.game).thenReturn(game)
        whenever(reader.readLine()).thenReturn("go")
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
        val spaceship = mockSpaceship()
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(room.game).thenReturn(game)
        whenever(reader.readLine()).thenReturn(command)
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
        val spaceship = mockSpaceship()

        val game = mock<Game>()
        whenever(game.isAsteroidAheadOf(eq(spaceship))).thenReturn(true)

        val room = mock<Room>()
        whenever(room.game).thenReturn(game)
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("isAsteroid")
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
        val spaceship = mockSpaceship()

        val game = mock<Game>()
        whenever(game.isGarbageAheadOf(eq(spaceship))).thenReturn(false)

        val room = mock<Room>()
        whenever(room.game).thenReturn(game)
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("isGarbage")
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
        val spaceship = mockSpaceship()

        val game = mock<Game>()
        whenever(game.isWallAheadOf(eq(spaceship))).thenReturn(true)

        val room = mock<Room>()
        whenever(room.game).thenReturn(game)
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false, true)
        whenever(reader.readLine()).thenReturn("isWall")
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
        whenever(room.isFull).thenReturn(false)
        whenever(room.isGameFinished).thenReturn(false)
        whenever(room.game).thenReturn(game)
        whenever(reader.readLine()).thenAnswer { "go" }
        val userHandler = createUserHandler(reader, writer, room)

        val userId = UserId(1)
        val spaceship = Spaceship(Position(1, 1500), userId)
        userHandler.setSpaceship(spaceship)

        // when
        runUserHandlerUntilFinished(userHandler)

        // then
        assertEquals(Spaceship.Status.DESTROYED, spaceship.status)
        verify(writer).println("died")
        verify(writer).println("You have collected 0 score")
    }

    @Test
    fun `given spaceship with score - when asteroid crash handled - then score decreased by 50`() {
        // given
        val room = mockRoom(mock<Game>())
        val userHandler = createUserHandler(room = room)

        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)
        spaceship.score = Score(100)
        userHandler.setSpaceship(spaceship)

        // when
        userHandler.crash(Type.ASTEROID)

        // then
        assertEquals(50, spaceship.score.value())
    }

    @Test
    fun `given spaceship with zero score - when asteroid crash handled - then spaceship destroyed`() {
        // given
        val room = mockRoom(mock<Game>())
        val userHandler = createUserHandler(room = room)

        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)
        userHandler.setSpaceship(spaceship)

        // when
        userHandler.crash(Type.ASTEROID)

        // then
        assertEquals(-50, spaceship.score.value())
        assertEquals(Spaceship.Status.DESTROYED, spaceship.status)
        assertFalse(spaceship.isVisible)
    }

    @Test
    fun `given spaceship crashed into garbage - when garbage crash handled - then score increased by 10 and game counter incremented`() {
        // given
        val game = mock<Game>()
        whenever(game.incrementCollectedGarbageCount()).thenReturn(1)
        whenever(game.garbageNumber).thenReturn(10)
        val room = mockRoom(game)
        val userHandler = createUserHandler(room = room)

        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)
        userHandler.setSpaceship(spaceship)

        // when
        userHandler.crash(Type.GARBAGE)

        // then
        assertEquals(10, spaceship.score.value())
        verify(game).incrementCollectedGarbageCount()
    }

    @Test
    fun `given spaceship collected last garbage - when garbage crash handled - then score increased by 10 and game counter incremented`() {
        // given
        val game = mock<Game>()
        whenever(game.incrementCollectedGarbageCount()).thenReturn(5)
        whenever(game.garbageNumber).thenReturn(5)
        val room = mockRoom(game)
        val userHandler = createUserHandler(room = room)

        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)
        userHandler.setSpaceship(spaceship)

        // when
        userHandler.crash(Type.GARBAGE)

        // then
        assertEquals(10, spaceship.score.value())
        verify(game).incrementCollectedGarbageCount()
    }

    @Test
    fun `given spaceship crashed into wall - when wall crash handled - then coordinates rolled back and score decreased by 50`() {
        // given
        val room = mockRoom(mock<Game>())
        val userHandler = createUserHandler(room = room)

        val userId = UserId(1)
        val spaceship = Spaceship(Position(5, 5), userId)
        spaceship.score = Score(100)
        spaceship.direction = Direction.UP
        spaceship.go()
        userHandler.setSpaceship(spaceship)

        // when
        userHandler.crash(Type.WALL)

        // then
        assertEquals(Position(5, 5), spaceship.position)
        assertEquals(50, spaceship.score.value())
    }

    private fun createUserHandler(
        reader: BufferedReader = mock(),
        writer: PrintWriter = mock(),
        room: Room = mock(),
        user: User = User(UserId(1), "alice"),
    ): UserHandler {
        whenever(room.addUser(any())).thenReturn(room)
        return UserHandler(reader, writer, room, user)
    }

    private fun mockRoom(game: Game): Room {
        val room = mock<Room>()
        whenever(room.game).thenReturn(game)
        return room
    }

    private fun mockSpaceship(): Spaceship {
        val spaceship = mock<Spaceship>()
        whenever(spaceship.steps).thenReturn(Steps())
        whenever(spaceship.score).thenReturn(Score(100))
        whenever(spaceship.status).thenReturn(Spaceship.Status.ALIVE)
        return spaceship
    }

    private fun runUserHandlerUntilFinished(userHandler: UserHandler) {
        val thread = Thread { userHandler.run() }
        thread.start()
        thread.join(5000)
    }
}
