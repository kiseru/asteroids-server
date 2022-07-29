package com.kiseru.asteroids.server.logics;

import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;

import java.util.Arrays;

/**
 * @author Bulat Giniyatullin
 * 09 Декабрь 2017
 */

public class Screen {
    private int width;
    private int height;
    private String[][] mainMatrix;

    public Screen(int width, int height) {
        this.width = width;
        this.height = height;
        mainMatrix = new String[height + 2][width + 2];
        generateClearScreen();
    }

    /**
     * рисует на экране точку
     * @param coordinates - по какой координате
     * @param symbol - символ, которым отображается точка
     */
    public void draw(Coordinates coordinates, String symbol) {
        if (mainMatrix[coordinates.getY()][coordinates.getX()].equals("."))
            mainMatrix[coordinates.getY()][coordinates.getX()] = symbol;
        else
            mainMatrix[coordinates.getY()][coordinates.getX()] =
                    String.format("%s|%s", mainMatrix[coordinates.getY()][coordinates.getX()], symbol);
    }

    /**
     * обновляет экран
     */
    public void update() {
        generateClearScreen();
    }

    /**
     * отображает экран
     */
    public String display() {
        StringBuilder result = new StringBuilder("");
        for (int i = 1; i < height + 1; i++) {
            for (int j = 1; j < width + 1; j++) {
                result.append(mainMatrix[i][j]);
                result.append("\t");
            }
            result.append("\n");
        }
        return result.toString();
    }

    private void generateClearScreen() {
        for (int i = 0; i < height + 2; i++) {
            if (i == 0 || i == height + 1) {
                Arrays.fill(mainMatrix[i], "*");
            } else {
                Arrays.fill(mainMatrix[i], ".");
                mainMatrix[i][0] = "*";
                mainMatrix[i][width + 1] = "*";
            }
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
