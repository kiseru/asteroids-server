package com.kiseru.asteroids.server.factory

import com.kiseru.asteroids.server.command.CommandHandler
import com.kiseru.asteroids.server.command.direction.impl.DownCommandHandler
import com.kiseru.asteroids.server.command.direction.impl.LeftCommandHandler
import com.kiseru.asteroids.server.command.direction.impl.RightCommandHandler
import com.kiseru.asteroids.server.command.direction.impl.UpCommandHandler
import com.kiseru.asteroids.server.command.factory.CommandHandlerFactory
import com.kiseru.asteroids.server.command.factory.impl.CommandHandlerFactoryImpl
import com.kiseru.asteroids.server.command.impl.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CommandHandlerFactoryTest {

    private lateinit var underTest: CommandHandlerFactory

    @BeforeEach
    fun setUp() {
        underTest = CommandHandlerFactoryImpl()
    }

    @Test
    fun testCreatingGoCommandHandler() {
        testCreatingHandler("go", GoCommandHandler::class.java)
    }

    @Test
    fun testCreatingLeftCommandHandler() {
        testCreatingHandler("left", LeftCommandHandler::class.java)
    }

    @Test
    fun testCreatingRightCommandHandler() {
        testCreatingHandler("right", RightCommandHandler::class.java)
    }

    @Test
    fun testCreatingUpCommandHandler() {
        testCreatingHandler("up", UpCommandHandler::class.java)
    }

    @Test
    fun testCreatingDownCommandHandler() {
        testCreatingHandler("down", DownCommandHandler::class.java)
    }

    @Test
    fun testCreatingIsAsteroidCommandHandler() {
        testCreatingHandler("isAsteroid", IsAsteroidCommandHandler::class.java)
    }

    @Test
    fun testCreatingIsGarbageCommandHandler() {
        testCreatingHandler("isGarbage", IsGarbageCommandHandler::class.java)
    }

    @Test
    fun testCreatingIsWallCommandHandler() {
        testCreatingHandler("isWall", IsWallCommandHandler::class.java)
    }

    @Test
    fun testCreatingExitCommandHandler() {
        testCreatingHandler("exit", ExitCommandHandler::class.java)
    }

    @Test
    fun testCreatingUnknownCommandHandler() {
        testCreatingHandler("unknown", UnknownCommandHandler::class.java)
    }

    private fun testCreatingHandler(command: String, expected: Class<out CommandHandler>) {
        val actual = underTest.create(command)
        Assertions.assertThat(actual).isInstanceOf(expected)
    }
}
