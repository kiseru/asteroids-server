package com.kiseru.asteroids.server.command

import com.fasterxml.jackson.databind.ObjectMapper
import com.kiseru.asteroids.server.command.impl.ExitCommandHandler
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.MessageSenderService
import com.kiseru.asteroids.server.service.impl.MessageSenderServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.ByteArrayOutputStream
import java.io.IOException

internal class ExitCommandHandlerTest {


    private lateinit var outputStream: ByteArrayOutputStream

    private lateinit var messageSenderService: MessageSenderService

    private lateinit var underTest: CommandHandler

    @Mock
    private lateinit var user: User

    @Mock
    private lateinit var spaceship: Spaceship

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        outputStream = ByteArrayOutputStream()
        messageSenderService = MessageSenderServiceImpl(ObjectMapper(), outputStream)
        underTest = ExitCommandHandler()
    }

    @AfterEach
    fun tearDown() {
        outputStream.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test handling exit command`() = runTest {
        underTest.handle(user, messageSenderService, spaceship) {}

        val actual = String(outputStream.toByteArray()).trim()

        val expected = "exit"
        assertThat(actual).isEqualTo(expected)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test handling exit command while io exception was thrown`() = runTest {
        try {
            underTest.handle(user, messageSenderService, spaceship) { throw IOException("some cool exception") }
        } catch (e: RuntimeException) {
            assertThat(e.cause).isNotNull
            assertThat(e.cause?.message).isEqualTo("some cool exception")
        }
    }
}
