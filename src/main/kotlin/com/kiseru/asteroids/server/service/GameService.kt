package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Player
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.Type
import java.io.OutputStream
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock

interface GameService {

    fun writeRatings(outputStream: OutputStream)

    fun writeGameFields(outputStream: OutputStream)

    fun writeGameField(game: Game, outputStream: OutputStream)

    fun writeGameField(game: Game, onMessageSend: (String) -> Unit)

    fun createGameHandler(lock: Lock, condition: Condition): (Game) -> Unit

    fun getGameRating(game: Game): String

    fun damageSpaceship(game: Game, player: Player, spaceship: Spaceship, type: Type)

    fun addGame(game: Game)
}
