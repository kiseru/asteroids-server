package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.impl.UserServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class UserServiceTest {

    private val underTest: UserService = UserServiceImpl()

    private lateinit var closeable: AutoCloseable

    @BeforeEach
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @AfterEach
    fun tearDown() {
        closeable.close()
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test creating user`(): Unit = runTest {

        val actual = underTest.createUser("some cool username")

        assertThat(actual.id).isNotNull
        assertThat(actual.username).isEqualTo("some cool username")

        val userStorage = ReflectionTestUtils.getField(underTest, "userStorage") as Map<UUID, User>
        val savedUser = userStorage[actual.id]
        assertThat(actual).isEqualTo(savedUser)
    }

    @Test
    fun `test finding user while it is not in storage`(): Unit = runTest {
        val actual = underTest.findUserById(UUID.randomUUID())

        assertThat(actual).isNull()
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test finding user while it is in storage`(): Unit = runTest {
        val userStorage = ReflectionTestUtils.getField(underTest, "userStorage") as MutableMap<UUID, User>
        val user = User(UUID.randomUUID(), "some cool username")
        userStorage[user.id] = user

        val actual = underTest.findUserById(user.id)

        assertThat(actual).isNotNull
        assertThat(actual).isEqualTo(user)
    }
}
