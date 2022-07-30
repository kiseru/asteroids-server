package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.model.Coordinates;
import com.kiseru.asteroids.server.model.Point;
import com.kiseru.asteroids.server.model.Type;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Garbage extends Point {
    public Garbage(Coordinates coordinates) {
        super(coordinates);
    }

    @Override
    public Type getType() {
        return Type.GARBAGE;
    }

    @Override
    public String getSymbolToShow() {
        return "G";
    }
}
