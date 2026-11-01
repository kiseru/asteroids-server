package com.kiseru.asteroids.server.messaging

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameMessageBroadcasterTest {

    @Test
    fun `test broadcast delivers message to all recipients of the same broadcaster`() {
        // given
        val firstRecipientMessages = mutableListOf<String>()
        val secondRecipientMessages = mutableListOf<String>()

        val broadcaster = GameMessageBroadcaster()
        broadcaster.addRecipient(firstRecipientMessages::add)
        broadcaster.addRecipient(secondRecipientMessages::add)

        // when
        broadcaster.broadcast("start")

        // then
        assertEquals(listOf("start"), firstRecipientMessages)
        assertEquals(listOf("start"), secondRecipientMessages)
    }

    @Test
    fun `test broadcast when each user has their own broadcaster`() {
        // given
        val firstUserMessages = mutableListOf<String>()
        val secondUserMessages = mutableListOf<String>()

        val firstUserBroadcaster = GameMessageBroadcaster()
        firstUserBroadcaster.addRecipient(firstUserMessages::add)

        val secondUserBroadcaster = GameMessageBroadcaster()
        secondUserBroadcaster.addRecipient(secondUserMessages::add)

        // when
        firstUserBroadcaster.broadcast("start")

        // then
        // known bug: a broadcaster is created per connection, so the message
        // never reaches other users — broadcasting is fake, it's unicast
        assertEquals(listOf("start"), firstUserMessages)
        assertTrue(secondUserMessages.isEmpty())
    }
}
