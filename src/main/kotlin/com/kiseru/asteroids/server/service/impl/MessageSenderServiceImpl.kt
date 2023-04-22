package com.kiseru.asteroids.server.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.kiseru.asteroids.server.awaitPrintln
import com.kiseru.asteroids.server.dto.ScoreDto
import com.kiseru.asteroids.server.model.User
import com.kiseru.asteroids.server.service.MessageSenderService
import java.io.OutputStream
import java.io.PrintWriter

class MessageSenderServiceImpl(
    private val objectMapper: ObjectMapper,
    outputStream: OutputStream,
) : MessageSenderService {

    private val writer = PrintWriter(outputStream, true)

    override suspend fun sendExit() {
        writer.awaitPrintln("exit")
    }

    override suspend fun sendScore(score: Int) {
        val scoreDto = ScoreDto(score)
        val msg = objectMapper.writeValueAsString(scoreDto)
        send(msg)
    }

    override suspend fun sendUnknownCommand() {
        send("Unknown command")
    }

    override suspend fun send(boolean: Boolean) {
        writer.awaitPrintln(if (boolean) "t" else "f")
    }

    override suspend fun send(message: String) {
        writer.awaitPrintln(message)
    }

    override suspend fun sendGameOver(score: Int) {
        writer.awaitPrintln("died")
        writer.awaitPrintln("You have collected $score score.")
    }

    override suspend fun sendWelcomeMessage() {
        writer.awaitPrintln("Welcome to Asteroids Server")
        writer.awaitPrintln("Please, introduce yourself!")
    }

    override suspend fun sendInstructions(user: User) {
        writer.awaitPrintln("You need to keep a space garbage.")
        writer.awaitPrintln("Your ID is ${user.id}")
        writer.awaitPrintln("Good luck, Commander!")
    }
}