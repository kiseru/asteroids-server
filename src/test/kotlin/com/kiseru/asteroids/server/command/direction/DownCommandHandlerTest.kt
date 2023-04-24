package com.kiseru.asteroids.server.command.direction

import com.fasterxml.jackson.databind.ObjectMapper
import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.command.direction.impl.DownCommandHandler
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.impl.MessageSenderServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.ByteArrayOutputStream


internal class DownCommandHandlerTest {

    private lateinit var outputStream: ByteArrayOutputStream

    private lateinit var messageSenderService: MessageSenderService

    private lateinit var underTest: CommandHandler

    @Mock
    private lateinit var user: User

    @Mock
    private lateinit var spaceship: Spaceship

    @Mock
    private lateinit var room: Room

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        outputStream = ByteArrayOutputStream()
        messageSenderService = MessageSenderServiceImpl(ObjectMapper(), outputStream)
        underTest = DownCommandHandler()
        given(user.room).willReturn(room)
    }

    @AfterEach
    fun tearDown() {
        outputStream.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test handling down command`() = runTest {
        given(user.score).willReturn(100)

        underTest.handle(user, messageSenderService, spaceship) {}

        val actual = String(outputStream.toByteArray()).trim()

        val expected = "{\"score\":100}"
        assertThat(actual).isEqualTo(expected)
    }
}
