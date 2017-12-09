package com.tutorteam.logics;

import com.tutorteam.logics.auxiliary.Coordinates;
import com.tutorteam.logics.models.*;
import com.tutorteam.server.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Game {
    private List<Model> gameObjects;
    private List<Point> pointsOnScreen;
    private Screen screen;

    /**
     * Конструктор создания игровой сессии
     * @param screen - экран
     * @param garbageNumber - количество мусора для первоначальной генерации
     * @param asteroidNumber - количество астероидов для первоначальной генерации
     */
    public Game(Screen screen, int garbageNumber, int asteroidNumber) {
        this.screen = screen;
        this.pointsOnScreen = new ArrayList<>();

        // генерируем мусор
        for (int i = 0; i < garbageNumber; i++) {
            Garbage garbage = new Garbage(generateUniqueRandomCoordinates());
            pointsOnScreen.add(garbage);
            gameObjects.add(garbage);
        }
        // генерируем астероиды
        for (int i = 0; i < asteroidNumber; i++) {
            Asteroid asteroid = new Asteroid(generateUniqueRandomCoordinates());
            pointsOnScreen.add(asteroid);
            gameObjects.add(asteroid);
        }
    }

    /**
     * создает и регистрирует новый корабль
     * @param user - юзер, для которого регистриуется корабль
     */
    public void registerSpaceShipForUser(User user) {
        SpaceShip spaceShip = new SpaceShip(generateUniqueRandomCoordinates(), user);
        this.pointsOnScreen.add(spaceShip);
        this.gameObjects.add(spaceShip);
    }

    private Coordinates generateUniqueRandomCoordinates() {
        Random random = new Random();
        Coordinates randomCoordinates = null;
        // если по случайно сгенерованным координатам уже что-то находится(или они ещё не сгенерированы)
        while (randomCoordinates == null || isGameObjectsContainsCoordinates(randomCoordinates))
            // генерируем новые координаты
            randomCoordinates = new Coordinates(random.nextInt(screen.getWidth()), random.nextInt(screen.getHeight()));
        return randomCoordinates;
    }

    private boolean isGameObjectsContainsCoordinates(Coordinates coordinates) {
        return pointsOnScreen.stream()
                .anyMatch(p -> p.getCoordinates().equals(coordinates));
    }

    public List<Model> getGameObjects() {
        return gameObjects;
    }

    public List<Point> getPointsOnScreen() {
        return pointsOnScreen;
    }
}