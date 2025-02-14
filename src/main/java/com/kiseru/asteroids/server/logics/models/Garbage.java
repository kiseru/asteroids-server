package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.logics.Screen;
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.logics.auxiliary.Type;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Garbage extends Point implements Crashable {
    public Garbage(Coordinates coordinates) {
        super(coordinates);
    }

    public void crash() {
        this.isVisible = false;
    }

    @Override
    public String view() {
        return "G";
    }

    @Override
    public Type getType() {
        return Type.GARBAGE;
    }
}
