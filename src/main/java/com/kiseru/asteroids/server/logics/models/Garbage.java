package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.logics.Screen;
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.logics.auxiliary.Type;
import com.kiseru.asteroids.server.model.Destroyable;
import com.kiseru.asteroids.server.model.Renderable;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Garbage extends Point implements Renderable, Destroyable {
    public Garbage(Coordinates coordinates) {
        super(coordinates);
    }

    public void destroy() {
        this.isVisible = false;
    }

    @Override
    public void render(Screen screen) {
        if (isVisible) {
            screen.draw(coordinates, "G");
        }
    }

    @Override
    public Type getType() {
        return Type.GARBAGE;
    }
}
