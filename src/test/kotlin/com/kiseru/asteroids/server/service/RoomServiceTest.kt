package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.game.GameService
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.service.impl.RoomServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.springframework.test.util.ReflectionTestUtils
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
internal class RoomServiceTest {

    private lateinit var closeable: AutoCloseable

    private lateinit var underTest: RoomService

    @Mock
    private lateinit var gameService: GameService

    private lateinit var roomStorage: MutableMap<UUID, Room>

    @BeforeEach
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        underTest = RoomServiceImpl(gameService)
        roomStorage = mutableMapOf()
        ReflectionTestUtils.setField(underTest, "roomStorage", roomStorage)
    }

    @AfterEach
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun `test getting not full room while room storage is empty`() = runTest {
        val game = mock(Game::class.java)
        given(gameService.createGame()).willReturn(game)

        val actual = underTest.getNotFullRoom()

        assertThat(roomStorage).isNotEmpty
        assertThat(roomStorage[actual.id]).isNotNull
    }

    @Test
    fun `test getting not full room while it is in the room storage`() = runTest {
        val room = mock(Room::class.java)
        val roomId = UUID.randomUUID()
        given(room.id).willReturn(roomId)
        given(room.isFull).willReturn(false)
        roomStorage[roomId] = room

        val actual = underTest.getNotFullRoom()

        assertThat(actual).isEqualTo(room)
    }
}
