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

    private val writer = PrintWriter(outputStream)

    override fun sendExit() {
        send("exit")
    }

    override fun sendScore(score: Int) {
        val scoreDto = ScoreDto(score)
        val msg = objectMapper.writeValueAsString(scoreDto)
        send(msg)
    }

    override fun sendUnknownCommand() {
        send("Unknown command")
    }

    override fun send(message: String) {
        writer.println(message)
        writer.flush()
    }

    override fun sendGameOver(score: Int) {
        writer.println("died")
        writer.println("You have collected $score score.")
        writer.flush()
    }

    override fun sendWelcomeMessage() {
        writer.println("Welcome To Asteroids Server")
        writer.println("Please, introduce yourself!")
        writer.flush()
    }

    override suspend fun sendInstructions(user: User) {
        writer.awaitPrintln("You need to keep a space garbage.")
        writer.awaitPrintln("Your ID is ${user.id}")
        writer.awaitPrintln("Good luck, Commander!")
    }
}