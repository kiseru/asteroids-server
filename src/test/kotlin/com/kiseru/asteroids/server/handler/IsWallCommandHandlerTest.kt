package com.kiseru.asteroids.server.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.kiseru.asteroids.server.handler.impl.IsWallCommandHandler
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.impl.MessageSenderServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.io.ByteArrayOutputStream

internal class IsWallCommandHandlerTest {

    private val commandHandler = IsWallCommandHandler()

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
        val user = Mockito.mock(User::class.java)
        Mockito.doReturn(true)
            .`when`(user)
            .isWallInFrontOfSpaceship

        commandHandler.handle(user, messageSenderService) {}
        val sentMessage = String(outputStream.toByteArray()).trim()
        assertEquals("t", sentMessage)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testHandleFalse() = runTest {
        val user = Mockito.mock(User::class.java)
        Mockito.doReturn(false)
            .`when`(user)
            .isWallInFrontOfSpaceship

        commandHandler.handle(user, messageSenderService) {}
        val sentMessage = String(outputStream.toByteArray()).trim()
        assertEquals("f", sentMessage)
    }
}
