package com.kiseru.asteroids.server.command.factory;

import com.kiseru.asteroids.server.command.CommandHandler;

public interface CommandHandlerFactory {

    CommandHandler create(String command);
}
