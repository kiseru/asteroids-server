package com.kiseru.asteroids.server.model

import java.util.*

class Screen(val width: Int, val height: Int) {

    private val mainMatrix: Array<Array<String>> = Array(height + 2) { Array(width + 2) { "." } }

    init {
        generateClearScreen()
    }

    /**
     * Рисует на экране точку.
     *
     * @param coordinates - по какой координате
     * @param point - точка, которую необходимо отобразить
     */
    fun draw(coordinates: Coordinates, point: Point) {
        val symbol = point.symbolToShow
        if (mainMatrix[coordinates.y][coordinates.x] == ".") {
            mainMatrix[coordinates.y][coordinates.x] = symbol
        } else {
            mainMatrix[coordinates.y][coordinates.x] =
                String.format("%s|%s", mainMatrix[coordinates.y][coordinates.x], symbol)
        }
    }

    /**
     * Обновляет экран.
     */
    fun update() {
        generateClearScreen()
    }

    /**
     * Отображает экран.
     */
    fun display(): String {
        val result = StringBuilder("")
        for (i in 1 until height + 1) {
            for (j in 1 until width + 1) {
                result.append(mainMatrix[i][j])
                result.append("\t")
            }
            result.append("\n")
        }
        return result.toString()
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
}