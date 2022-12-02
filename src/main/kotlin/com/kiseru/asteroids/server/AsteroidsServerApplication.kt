package com.kiseru.asteroids.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

@SpringBootApplication
@ConfigurationPropertiesScan("com.kiseru.asteroids.server.properties")
class AsteroidsServerApplication

suspend fun main(args: Array<String>) {
    val context = runApplication<AsteroidsServerApplication>(*args)
    val server = context.getBean(Server::class.java)
    server.startServer()
}

suspend fun ServerSocket.awaitAccept(): Socket = withContext(Dispatchers.IO) { accept() }

suspend fun Socket.awaitInputStream(): InputStream = withContext(Dispatchers.IO) { getInputStream() }

suspend fun Socket.awaitOutputStream(): OutputStream = withContext(Dispatchers.IO) { getOutputStream() }

suspend fun PrintWriter.awaitPrintln(x: String): Unit = withContext(Dispatchers.IO) { println(x) }
