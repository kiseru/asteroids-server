package com.kiseru.asteroids.server.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
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

    override fun sendInstructions(user: User) {
        writer.println("You need to keep a space garbage.")
        writer.println("Your ID is ${user.id}")
        writer.println("Good luck, Commander!")
        writer.flush()
    }
}