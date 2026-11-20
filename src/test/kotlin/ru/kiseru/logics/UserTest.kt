package ru.kiseru.logics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserTest {

    @Test
    fun `given empty or null username - when user created - then exception thrown`() {
        // when & then
        assertThrows<IllegalArgumentException> { User("") }
        assertThrows<IllegalArgumentException> { User(null) }
    }

    @Test
    fun `given valid username - when user created - then username stored`() {
        // given
        val username = "alice"

        // when
        val user = User(username)

        // then
        assertEquals(username, user.username)
    }
}
