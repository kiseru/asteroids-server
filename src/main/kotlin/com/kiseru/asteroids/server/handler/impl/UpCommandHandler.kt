package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.handler.DirectionCommandHandler
import com.kiseru.asteroids.server.model.Direction

class UpCommandHandler : DirectionCommandHandler {

    override suspend fun handle(user: User) = handleDirection(user, Direction.UP)
}