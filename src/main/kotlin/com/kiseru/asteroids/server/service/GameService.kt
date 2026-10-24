package com.kiseru.asteroids.server.service

import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.Spaceship
import com.kiseru.asteroids.server.model.Type
import java.io.OutputStream

interface GameService {

    fun writeRatings(outputStream: OutputStream)

    fun writeGameFields(outputStream: OutputStream)

    fun writeGameField(game: Game, outputStream: OutputStream)

    fun writeGameField(game: Game, onMessageSend: (String) -> Unit)

    fun getGameRating(game: Game): String

    fun damageSpaceship(game: Game, spaceship: Spaceship, type: Type)
}
