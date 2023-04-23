package com.kiseru.asteroids.server.factory.command.impl;

import com.kiseru.asteroids.server.command.CommandHandler;

public interface CommandHandlerFactory {

    CommandHandler create(String command);
}
