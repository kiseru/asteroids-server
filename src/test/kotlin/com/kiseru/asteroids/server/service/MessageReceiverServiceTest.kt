package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.service.impl.MessageReceiverServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

internal class MessageReceiverServiceTest {

    private lateinit var inputStream: ByteArrayInputStream

    private lateinit var underTest: MessageReceiverService

    @BeforeEach
    fun setUp() {
        inputStream = ByteArrayInputStream("some cool message\n".toByteArray())
        underTest = MessageReceiverServiceImpl(inputStream)
    }

    @AfterEach
    fun tearDown() {
        inputStream.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test receiving message`() = runTest {
        val actual = underTest.receive()

        val expected = "some cool message"
        assertThat(actual).isEqualTo(expected)
    }
}
