package ru.kiseru.logics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserTest {

    @Test
    fun `given empty or null username - when user created - then exception thrown`() {
        // when & then
        assertThrows<IllegalArgumentException> { User(1, "") }
        assertThrows<IllegalArgumentException> { User(1, null) }
    }

    @Test
    fun `given valid id and username - when user created - then id and username stored`() {
        // given
        val id = 1L
        val username = "alice"

        // when
        val user = User(id, username)

        // then
        assertEquals(id, user.id)
        assertEquals(username, user.username)
    }
}
