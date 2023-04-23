package com.kiseru.asteroids.server.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.kiseru.asteroids.server.handler.impl.IsGarbageCommandHandler
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

internal class IsGarbageCommandHandlerTest {

        lateinit var outputStream: ByteArrayOutputStream

        lateinit var messageSenderService: MessageSenderServiceImpl

        lateinit var underTest: CommandHandler

        @Mock
        lateinit var user: User

        @BeforeEach
        fun setUp() {
            MockitoAnnotations.openMocks(this)
            val objectMapper = ObjectMapper()
            outputStream = ByteArrayOutputStream()
            messageSenderService = MessageSenderServiceImpl(objectMapper, outputStream)
            underTest = IsGarbageCommandHandler()
        }

        @AfterEach()
        fun tearDown() {
            outputStream.close()
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        fun `test handling isGarbage command when garbage is on front of`() = runTest {
            given(user.isGarbageInFrontOfSpaceship).willReturn(true)

            underTest.handle(user, messageSenderService) {}
            val actual = String(outputStream.toByteArray()).trim()

            val expected = "t"
            assertThat(actual).isEqualTo(expected)
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        fun `test handling isGarbage command when garbage is not on front of`() = runTest {
            given(user.isGarbageInFrontOfSpaceship).willReturn(false)

            underTest.handle(user, messageSenderService) {}
            val actual = String(outputStream.toByteArray()).trim()

            val expected = "f"
            assertThat(actual).isEqualTo(expected)
        }
}
