package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.logics.Screen;
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.logics.auxiliary.Type;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Asteroid extends Point implements Model, Crashable{
    public Asteroid(Coordinates coordinates) {
        super(coordinates);
    }

    public void crash() {
        this.isVisible = false;
    }

    @Override
    public void render(Screen screen) {
        if (isVisible) {
            screen.draw(coordinates, "A");
        }
    }

    @Override
    public Type getType() {
        return Type.ASTEROID;
    }
}
