package com.kiseru.asteroids.server.service

import com.fasterxml.jackson.databind.ObjectMapper
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

internal class MessageSenderServiceTest {

    private lateinit var outputStream: ByteArrayOutputStream

    private lateinit var underTest: MessageSenderService

    @Mock
    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        outputStream = ByteArrayOutputStream()
        val objectMapper = ObjectMapper()
        underTest = MessageSenderServiceImpl(objectMapper, outputStream)
    }

    @AfterEach
    fun tearDown() {
        outputStream.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test send true`() = runTest {
        underTest.send(true)
        val actual = String(outputStream.toByteArray()).trim()

        val expected = "t"
        assertThat(actual).isEqualTo(expected)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test send false`() = runTest {
        underTest.send(false)
        val actual = String(outputStream.toByteArray()).trim()

        val expected = "f"
        assertThat(actual).isEqualTo(expected)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test send message`() = runTest {
        underTest.send("message")
        val actual = String(outputStream.toByteArray()).trim()

        val expected = "message"
        assertThat(actual).isEqualTo(expected)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test send game over message`() = runTest {
        underTest.sendGameOver(100)
        val actual = String(outputStream.toByteArray()).trim()

        val expected = "died${System.lineSeparator()}You have collected 100 score."
        assertThat(actual).isEqualTo(expected)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test send welcome message`() = runTest {
        underTest.sendWelcomeMessage()
        val actual = String(outputStream.toByteArray()).trim()

        val expected = "Welcome to Asteroids Server${System.lineSeparator()}Please, introduce yourself!"
        assertThat(actual).isEqualTo(expected)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test send instructions`() = runTest {
        given(user.id).willReturn("some cool id")

        underTest.sendInstructions(user)
        val actual = String(outputStream.toByteArray()).trim()

        val expected =
            "You need to keep a space garbage.${System.lineSeparator()}Your ID is some cool id${System.lineSeparator()}Good luck, Commander!"
        assertThat(actual).isEqualTo(expected)
    }
}
