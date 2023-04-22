package com.kiseru.asteroids.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.kiseru.asteroids.server.service.impl.MessageSenderServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

internal class MessageSenderServiceTest {

    lateinit var outputStream: ByteArrayOutputStream

    lateinit var messageSenderService: MessageSenderService

    @BeforeEach
    fun setUp() {
        outputStream = ByteArrayOutputStream()
        messageSenderService = MessageSenderServiceImpl(ObjectMapper(), outputStream)
    }

    @AfterEach
    fun tearDown() {
        outputStream.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSendBool() = runTest {
        messageSenderService.send(true)
        var sentMsg = String(outputStream.toByteArray()).trim()
        assertEquals("t", sentMsg)

        outputStream.reset()

        messageSenderService.send(false)
        sentMsg = String(outputStream.toByteArray()).trim()
        assertEquals("f", sentMsg)
    }
}
