package com.kiseru.asteroids.server.factory.command;

import com.kiseru.asteroids.server.command.CommandHandler;
import com.kiseru.asteroids.server.command.direction.impl.DownCommandHandler;
import com.kiseru.asteroids.server.command.impl.ExitCommandHandler;
import com.kiseru.asteroids.server.command.impl.GoCommandHandler;
import com.kiseru.asteroids.server.command.impl.IsAsteroidCommandHandler;
import com.kiseru.asteroids.server.command.impl.IsGarbageCommandHandler;
import com.kiseru.asteroids.server.command.impl.IsWallCommandHandler;
import com.kiseru.asteroids.server.command.direction.impl.LeftCommandHandler;
import com.kiseru.asteroids.server.command.direction.impl.RightCommandHandler;
import com.kiseru.asteroids.server.command.impl.UnknownCommandHandler;
import com.kiseru.asteroids.server.command.direction.impl.UpCommandHandler;
import com.kiseru.asteroids.server.factory.command.impl.CommandHandlerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CommandHandlerFactoryTest {

    private CommandHandlerFactory underTest;

    @BeforeEach
    void setUp() {
        underTest = new CommandHandlerFactoryImpl();
    }

    @Test
    void testCreatingGoCommandHandler() {
        testCreatingHandler("go", GoCommandHandler.class);
    }

    @Test
    void testCreatingLeftCommandHandler() {
        testCreatingHandler("left", LeftCommandHandler.class);
    }

    @Test
    void testCreatingRightCommandHandler() {
        testCreatingHandler("right", RightCommandHandler.class);
    }

    @Test
    void testCreatingUpCommandHandler() {
        testCreatingHandler("up", UpCommandHandler.class);
    }

    @Test
    void testCreatingDownCommandHandler() {
        testCreatingHandler("down", DownCommandHandler.class);
    }

    @Test
    void testCreatingIsAsteroidCommandHandler() {
        testCreatingHandler("isAsteroid", IsAsteroidCommandHandler.class);
    }

    @Test
    void testCreatingIsGarbageCommandHandler() {
        testCreatingHandler("isGarbage", IsGarbageCommandHandler.class);
    }

    @Test
    void testCreatingIsWallCommandHandler() {
        testCreatingHandler("isWall", IsWallCommandHandler.class);
    }

    @Test
    void testCreatingExitCommandHandler() {
        testCreatingHandler("exit", ExitCommandHandler.class);
    }

    @Test
    void testCreatingUnknownCommandHandler() {
        testCreatingHandler("unknown", UnknownCommandHandler.class);
    }

    private void testCreatingHandler(String command, Class<? extends CommandHandler> expected) {
        var actual = underTest.create(command);

        assertThat(actual).isInstanceOf(expected);
    }
}
