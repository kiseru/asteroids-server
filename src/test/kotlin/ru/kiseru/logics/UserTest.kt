package ru.kiseru.logics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserTest {

    @Test
    fun `given empty or null username - when user created - then exception thrown`() {
        // given
        val userId = UserId(1)

        // when & then
        assertThrows<IllegalArgumentException> { User(userId, "") }
        assertThrows<IllegalArgumentException> { User(userId, null) }
    }

    @Test
    fun `given valid id and username - when user created - then id and username stored`() {
        // given
        val userId = UserId(1L)
        val username = "alice"

        // when
        val user = User(userId, username)

        // then
        assertEquals(userId, user.id)
        assertEquals(username, user.username)
    }
}
