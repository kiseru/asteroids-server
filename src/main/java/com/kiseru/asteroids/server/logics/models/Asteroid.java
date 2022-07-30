package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.logics.Screen;
import com.kiseru.asteroids.server.model.Coordinates;
import com.kiseru.asteroids.server.model.Point;
import com.kiseru.asteroids.server.model.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Asteroid extends Point {

    public Asteroid(Coordinates coordinates) {
        super(coordinates);
    }

    @Override
    public Type getType() {
        return Type.ASTEROID;
    }

    @Override
    public String getSymbolToShow() {
        return "A";
    }
}
