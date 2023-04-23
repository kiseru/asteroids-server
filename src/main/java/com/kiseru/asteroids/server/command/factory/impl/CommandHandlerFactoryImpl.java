package com.kiseru.asteroids.server.command.factory.impl;

import com.kiseru.asteroids.server.command.CommandHandler;
import com.kiseru.asteroids.server.command.direction.impl.DownCommandHandler;
import com.kiseru.asteroids.server.command.factory.CommandHandlerFactory;
import com.kiseru.asteroids.server.command.impl.ExitCommandHandler;
import com.kiseru.asteroids.server.command.impl.GoCommandHandler;
import com.kiseru.asteroids.server.command.impl.IsAsteroidCommandHandler;
import com.kiseru.asteroids.server.command.impl.IsGarbageCommandHandler;
import com.kiseru.asteroids.server.command.impl.IsWallCommandHandler;
import com.kiseru.asteroids.server.command.direction.impl.LeftCommandHandler;
import com.kiseru.asteroids.server.command.direction.impl.RightCommandHandler;
import com.kiseru.asteroids.server.command.impl.UnknownCommandHandler;
import com.kiseru.asteroids.server.command.direction.impl.UpCommandHandler;
import org.springframework.stereotype.Component;

@Component
public class CommandHandlerFactoryImpl implements CommandHandlerFactory {

    @Override
    public CommandHandler create(String command) {
        return switch (command) {
            case "go" -> new GoCommandHandler();
            case "left" -> new LeftCommandHandler();
            case "right" -> new RightCommandHandler();
            case "up" -> new UpCommandHandler();
            case "down" -> new DownCommandHandler();
            case "isAsteroid" -> new IsAsteroidCommandHandler();
            case "isGarbage" -> new IsGarbageCommandHandler();
            case "isWall" -> new IsWallCommandHandler();
            case "exit" -> new ExitCommandHandler();
            default -> new UnknownCommandHandler();
        };
    }
}
