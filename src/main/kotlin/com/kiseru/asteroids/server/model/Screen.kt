package com.kiseru.asteroids.server.model

import java.util.Arrays

class Screen(
    val width: Int,
    val height: Int,
) {

    private val mainMatrix = Array(height + 2) { Array(width + 2) { "." } }

    init {
        generateClearScreen()
    }

    fun render(gameObject: GameObject) {
        if (gameObject.isVisible) {
            draw(gameObject)
        }
    }

    private fun draw(gameObject: GameObject): Unit =
        draw(gameObject.x, gameObject.y, gameObject.view())

    private fun draw(x: Int, y: Int, symbol: String) {
        if (symbol.isBlank()) {
            mainMatrix[y][x] = "."
        }

        if (mainMatrix[y][x] == ".") {
            mainMatrix[y][x] = symbol
        } else {
            mainMatrix[y][x] = "${mainMatrix[y][x]}|$symbol"
        }
    }

    fun update() {
        generateClearScreen()
    }

    private fun generateClearScreen() {
        for (i in 0 until height + 2) {
            if (i == 0 || i == height + 1) {
                Arrays.fill(mainMatrix[i], "*")
            } else {
                Arrays.fill(mainMatrix[i], ".")
                mainMatrix[i][0] = "*"
                mainMatrix[i][width + 1] = "*"
            }
        }
    }

    fun display(): String {
        val stringBuilder = StringBuilder()
        for (i in 1 until height + 1) {
            for (j in 1 until width + 1) {
                stringBuilder.append(mainMatrix[i][j])
                stringBuilder.append("\t")
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }
}