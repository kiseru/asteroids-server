package ru.kiseru.logics;

import java.util.Arrays;
import ru.kiseru.logics.auxiliary.Position;
import ru.kiseru.logics.auxiliary.ScreenSize;

public class Screen {

    private final ScreenSize size;
    private final String[][] mainMatrix;

    public Screen(ScreenSize size) {
        this.size = size;
        mainMatrix = new String[size.height() + 2][size.width() + 2];
        generateClearScreen();
    }

    /**
     * Рисует на экране точку
     * @param position - по какой координате
     * @param symbol - символ, которым отображается точка
     */
    public synchronized void draw(Position position, String symbol) {
        if (mainMatrix[position.y()][position.x()].equals(".")) {
            mainMatrix[position.y()][position.x()] = symbol;
        } else {
            mainMatrix[position.y()][position.x()] =
                String.format("%s|%s", mainMatrix[position.y()][position.x()], symbol);
        }
    }

    /**
     * Обновляет экран
     */
    public synchronized void update() {
        generateClearScreen();
    }

    /**
     * Отображает экран
     */
    public synchronized String display() {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < size.height() + 1; i++) {
            for (int j = 1; j < size.width() + 1; j++) {
                result.append(mainMatrix[i][j]);
                result.append("\t");
            }
            result.append("\n");
        }
        return result.toString();
    }

    private void generateClearScreen() {
        for (int i = 0; i < size.height() + 2; i++) {
            if (i == 0 || i == size.height() + 1) {
                Arrays.fill(mainMatrix[i], "*");
            } else {
                Arrays.fill(mainMatrix[i], ".");
                mainMatrix[i][0] = "*";
                mainMatrix[i][size.width() + 1] = "*";
            }
        }
    }

    public ScreenSize getSize() {
        return size;
    }
}
