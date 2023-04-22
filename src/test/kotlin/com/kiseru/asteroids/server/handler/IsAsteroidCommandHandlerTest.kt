package com.kiseru.asteroids.server.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.kiseru.asteroids.server.handler.impl.IsAsteroidCommandHandler
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.impl.MessageSenderServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import java.io.ByteArrayOutputStream

internal class IsAsteroidCommandHandlerTest {

    private val commandHandler = IsAsteroidCommandHandler()

    lateinit var outputStream: ByteArrayOutputStream

    lateinit var messageSenderService: MessageSenderServiceImpl

    @BeforeEach
    fun setUp() {
        val objectMapper = ObjectMapper()
        outputStream = ByteArrayOutputStream()
        messageSenderService = MessageSenderServiceImpl(objectMapper, outputStream)
    }

    @AfterEach()
    fun tearDown() {
        outputStream.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testHandleTrue() = runTest {
        val user = mock(User::class.java)
        doReturn(true)
            .`when`(user)
            .isAsteroidInFrontOfSpaceship

        commandHandler.handle(user, messageSenderService) {}
        val sentMessage = String(outputStream.toByteArray()).trim()
        assertEquals("t", sentMessage)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testHandleFalse() = runTest {
        val user = mock(User::class.java)
        doReturn(false)
            .`when`(user)
            .isAsteroidInFrontOfSpaceship

        commandHandler.handle(user, messageSenderService) {}
        val sentMessage = String(outputStream.toByteArray()).trim()
        assertEquals("f", sentMessage)
    }
}
