package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.logics.Screen;
import com.kiseru.asteroids.server.model.Coordinates;
import com.kiseru.asteroids.server.model.Destroyable;
import com.kiseru.asteroids.server.model.Renderable;
import com.kiseru.asteroids.server.model.Type;

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
