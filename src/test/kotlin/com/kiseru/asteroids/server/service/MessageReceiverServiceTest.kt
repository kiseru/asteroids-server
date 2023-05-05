package com.kiseru.asteroids.server.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kiseru.asteroids.server.model.Message
import com.kiseru.asteroids.server.service.impl.MessageReceiverServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

@OptIn(ExperimentalCoroutinesApi::class)
internal class MessageReceiverServiceTest {

    private lateinit var inputStream: ByteArrayInputStream

    private lateinit var underTest: MessageReceiverService

    @BeforeEach
    fun setUp() {
        val messages = """
            {"token":"token","command":"message #1"}
            {"token":"token","command":"message #2"}
            {"token":"token","command":"message #3"}
        """.trimIndent()
        inputStream = ByteArrayInputStream(messages.toByteArray())
        underTest = MessageReceiverServiceImpl(jacksonObjectMapper(), inputStream)
    }

    @AfterEach
    fun tearDown() {
        inputStream.close()
    }

    @Test
    fun `test receiving message`() = runTest {
        val actual = underTest.receive()

        val expected = "{\"token\":\"token\",\"message\":\"message #1\"}"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `test receiving json message`(): Unit = runTest {
        val actual = underTest.receiveMessage()

        val expected = Message("token", "message #1")
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `test receiving message by flow`(): Unit = runTest {
        val msgFlow = underTest.receivingMessagesFlow()
        val expectedFlow = listOf(
            Message("token", "message #1"),
            Message("token", "message #2"),
            Message("token", "message #3"),
        ).asFlow()

        msgFlow.zip(expectedFlow) { msg, expected -> Pair(msg, expected) }
            .collect { assertThat(it.first).isEqualTo(it.second) }
    }
}
