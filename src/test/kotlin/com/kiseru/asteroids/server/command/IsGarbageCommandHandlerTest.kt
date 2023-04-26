package com.kiseru.asteroids.server.command

import com.fasterxml.jackson.databind.ObjectMapper
import com.kiseru.asteroids.server.command.impl.IsGarbageCommandHandler
import com.kiseru.asteroids.server.model.Room
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.User
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

@OptIn(ExperimentalCoroutinesApi::class)
internal class IsGarbageCommandHandlerTest {

    private lateinit var outputStream: ByteArrayOutputStream

    private lateinit var messageSenderService: MessageSenderServiceImpl

    private lateinit var underTest: CommandHandler

    private lateinit var closeable: AutoCloseable

    @Mock
    private lateinit var user: User

    @Mock
    private lateinit var spaceship: Spaceship

    @Mock
    private lateinit var room: Room

    @BeforeEach
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        val objectMapper = ObjectMapper()
        outputStream = ByteArrayOutputStream()
        messageSenderService = MessageSenderServiceImpl(objectMapper, outputStream)
        underTest = IsGarbageCommandHandler()
    }

    @AfterEach()
    fun tearDown() {
        outputStream.close()
        closeable.close()
    }

    @Test
    fun `test handling isGarbage command when garbage is on front of`() = runTest {
        given(spaceship.isGarbageInFrontOf).willReturn(true)

        underTest.handle(user, room, messageSenderService, spaceship) {}
        val actual = String(outputStream.toByteArray()).trim()

        val expected = "t"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `test handling isGarbage command when garbage is not on front of`() = runTest {
        given(spaceship.isGarbageInFrontOf).willReturn(false)

        underTest.handle(user, room, messageSenderService, spaceship) {}
        val actual = String(outputStream.toByteArray()).trim()

        val expected = "f"
        assertThat(actual).isEqualTo(expected)
    }
}
