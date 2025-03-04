package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.model.Direction

interface SpaceshipHandler {

    fun onIncrementSteps()

    fun onSendInstructions()

    fun onRoomJoin(roomName: String)

    fun onSendScore()

    fun onSuccess()

    fun onIsAsteroid()

    fun onIsGarbage()

    fun onIsWall()

    fun onUnknownCommand()

    fun onSpaceshipMove()

    fun onSpaceshipChangeDirection(direction: Direction)
}