package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Player
import com.kiseru.asteroids.server.model.Spaceship

interface SpaceshipHandler {

    fun onIncrementSteps(player: Player)

    fun onSendInstructions(spaceship: Spaceship)

    fun onRoomJoin(roomName: String)

    fun onSendScore(player: Player)

    fun onSuccess()

    fun onIsAsteroid(player: Player, spaceship: Spaceship)

    fun onIsGarbage(player: Player, spaceship: Spaceship)

    fun onIsWall(player: Player, spaceship: Spaceship)

    fun onUnknownCommand()

    fun onSpaceshipMove(direction: Direction, spaceship: Spaceship)

    fun onSpaceshipChangeDirection(player: Player, direction: Direction)
}