package com.tutorteam.logics;

import com.tutorteam.logics.auxiliary.Coordinates;

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
        if (mainMatrix[coordinates.y + 1][coordinates.x + 1].equals("."))
            mainMatrix[coordinates.y + 1][coordinates.x + 1] = symbol;
        else
            mainMatrix[coordinates.y + 1][coordinates.x + 1] =
                    String.format("%s|%s", mainMatrix[coordinates.y + 1][coordinates.x + 1], symbol);
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
    public void display() {
        for (int i = 0; i < height; i++) {
            System.out.println("\n");
        }
        for (int i = 1; i < height + 1; i++) {
            for (int j = 0; j < width + 1; j++) {
                System.out.print(mainMatrix[i][j] + "\t");
            }
            System.out.println();
        }
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
