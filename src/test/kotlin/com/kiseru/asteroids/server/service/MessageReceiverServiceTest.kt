package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.service.impl.MessageReceiverServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import kotlin.math.exp

@OptIn(ExperimentalCoroutinesApi::class)
internal class MessageReceiverServiceTest {

    private lateinit var inputStream: ByteArrayInputStream

    private lateinit var underTest: MessageReceiverService

    @BeforeEach
    fun setUp() {
        val messages = """
            message #1
            message #2
            message #3
        """.trimIndent()
        inputStream = ByteArrayInputStream(messages.toByteArray())
        underTest = MessageReceiverServiceImpl(inputStream)
    }

    @AfterEach
    fun tearDown() {
        inputStream.close()
    }

    @Test
    fun `test receiving message`() = runTest {
        val actual = underTest.receive()

        val expected = "message #1"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `test receiving message by flow`(): Unit = runTest {
        val msgFlow = underTest.receivingFlow()
        val expectedFlow = listOf("message #1", "message #2", "message #3").asFlow()

        msgFlow.zip(expectedFlow) { msg, expected -> Pair(msg, expected) }
            .collect { assertThat(it.first).isEqualTo(it.second) }
    }
}
