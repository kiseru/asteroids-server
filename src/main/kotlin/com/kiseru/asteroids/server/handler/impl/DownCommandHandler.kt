package com.kiseru.asteroids.server.handler.impl

import com.kiseru.asteroids.server.User
import com.kiseru.asteroids.server.handler.DirectionCommandHandler
import com.kiseru.asteroids.server.model.Direction

class DownCommandHandler : DirectionCommandHandler {

    override fun handle(user: User) = handleDirection(user, Direction.DOWN)
}