package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.logics.auxiliary.Direction

interface UserHandler {

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
